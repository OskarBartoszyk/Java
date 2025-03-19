import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class MainFrame {
    
    private JFrame frame;
    // Ścieżka do katalogu z plikami tekstowymi
    private static final String DIR_PATH = "files";
    // Określa ile najczęściej występujących wyrazów bierzemy pod uwagę
    private final int liczbaWyrazowStatystyki;
    private final AtomicBoolean fajrant;
    private final int liczbaProducentow;
    private final int liczbaKonsumentow;
    // Pula wątków – obiekt klasy ExecutorService, który zarządza tworzeniem oraz recyklingiem wątków
    private ExecutorService executor;
    // Lista obiektów klasy Future, dzięki którym mamy możliwość nadzoru pracy wątków producenckich
    private List<Future<?>> producentFuture;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame window = new MainFrame();
                    window.frame.pack();
                    window.frame.setAlwaysOnTop(true);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MainFrame() {
        liczbaWyrazowStatystyki = 10;
        fajrant = new AtomicBoolean(false);
        liczbaProducentow = 1;
        liczbaKonsumentow = 2;
        executor = Executors.newFixedThreadPool(liczbaProducentow + liczbaKonsumentow);
        producentFuture = new ArrayList<>();
        initialize();
    }


    private void initialize() {
        frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                executor.shutdownNow();
            }
        });
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JButton btnStop = new JButton("Stop");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fajrant.set(true);
                for (Future<?> f : producentFuture) {
                    f.cancel(true);
                }
            }
        });

        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getMultiThreadedStatistics();
            }
        });

        JButton btnZamknij = new JButton("Zamknij");
        btnZamknij.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executor.shutdownNow();
                frame.dispose();
            }
        });

        panel.add(btnStart);
        panel.add(btnStop);
        panel.add(btnZamknij);
    }

    private void getMultiThreadedStatistics() {
        // Sprawdzamy, czy żaden producent nie działa
        for (Future<?> f : producentFuture) {
            if (!f.isDone()) {
                JOptionPane.showMessageDialog(frame, "Nie można uruchomić nowego zadania!\nPrzynajmniej jeden producent nadal działa!",
                        "OSTRZEŻENIE", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        fajrant.set(false);
        producentFuture.clear();

        // Kolejka dla ścieżek plików tekstowych
        final BlockingQueue<Optional<Path>> kolejka = new LinkedBlockingQueue<>(liczbaKonsumentow);
        final int przerwa = 60; // przerwa w sekundach

        // Definicja producenta
        Runnable producent = () -> {
            final String name = Thread.currentThread().getName();
            String info = String.format("PRODUCENT %s URUCHOMIONY ...", name);
            System.out.println(info);

            while (!Thread.currentThread().isInterrupted()) {
                if (fajrant.get()) {
                    // Wysyłamy poison pills do wszystkich konsumentów
                    try {
                        for (int i = 0; i < liczbaKonsumentow; i++) {
                            kolejka.put(Optional.empty());
                        }
                        break; // Zakończ pętlę producenta
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    // Przeszukiwanie katalogu i dodawanie plików *.txt do kolejki
                    try {
                        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.txt");
                        Files.walkFileTree(Paths.get(DIR_PATH), new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                                if (matcher.matches(path)) {
                                    try {
                                        Optional<Path> optPath = Optional.of(path);
                                        kolejka.put(optPath);
                                        System.out.println("Producent " + name + " dodał plik: " + path);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return FileVisitResult.TERMINATE;
                                    }
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (IOException e) {
                        System.out.println("Błąd przeszukiwania plików: " + e.getMessage());
                    }
                }
                info = String.format("Producent %s ponownie sprawdzi katalogi za %d sekund", name, przerwa);
                System.out.println(info);
                try {
                    TimeUnit.SECONDS.sleep(przerwa);
                } catch (InterruptedException e) {
                    info = String.format("Przerwa producenta %s przerwana!", name);
                    System.out.println(info);
                    if (!fajrant.get()) Thread.currentThread().interrupt();
                }
            }
            info = String.format("PRODUCENT %s SKOŃCZYŁ PRACĘ", name);
            System.out.println(info);
        };

        // Definicja konsumenta
        Runnable konsument = () -> {
            final String name = Thread.currentThread().getName();
            String info = String.format("KONSUMENT %s URUCHOMIONY ...", name);
            System.out.println(info);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Optional<Path> optPath = kolejka.take();
                    if (!optPath.isPresent()) {
                        // Odebrano poison pill, kończymy pracę
                        info = String.format("Konsument %s otrzymał poison pill. Kończenie pracy.", name);
                        System.out.println(info);
                        break;
                    }
                    Path path = optPath.get();
                    info = String.format("Konsument %s przetwarza plik: %s", name, path.getFileName());
                    System.out.println(info);
                    // Uzyskiwanie statystyk wyrazów
                    Map<String, Long> wordStats = getLinkedCountedWords(path, liczbaWyrazowStatystyki);
                    System.out.println(String.format("Plik %s - Top %d słów:", path.getFileName(), liczbaWyrazowStatystyki));
                    wordStats.forEach((word, count) ->
                        System.out.println(String.format("  %s: %d", word, count)));
                } catch (InterruptedException e) {
                    info = String.format("Oczekiwanie konsumenta %s przerwane!", name);
                    System.out.println(info);
                    Thread.currentThread().interrupt();
                }
            }
            info = String.format("KONSUMENT %s ZAKOŃCZYŁ PRACĘ", name);
            System.out.println(info);
        };

        // Uruchamianie wątków-producentów
        for (int i = 0; i < liczbaProducentow; i++) {
            Future<?> pf = executor.submit(producent);
            producentFuture.add(pf);
        }
        // Uruchamianie wątków-konsumentów
        for (int i = 0; i < liczbaKonsumentow; i++) {
            executor.execute(konsument);
        }
    }

    private Map<String, Long> getLinkedCountedWords(Path path, int wordsLimit) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return reader.lines() // Strumień linii z pliku
                // 1. Rozdzielenie linii na słowa
                .flatMap(line -> Stream.of(line.split("\\s+")))
                // 2. Usunięcie znaków nie będących literami lub cyframi
                .map(word -> word.replaceAll("[^a-zA-Z0-9ąęóśćżńźĄĘÓŚĆŻŃŹ]", ""))
                // 3. Filtrowanie słów - tylko te o długości co najmniej 3 znaków
                .filter(word -> word.matches("[a-zA-Z0-9ąęóśćżńźĄĘÓŚĆŻŃŹ]{3,}"))
                // 4. Konwersja na małe litery
                .map(String::toLowerCase)
                // 5. Grupowanie słów według częstotliwości
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                // 6. Sortowanie według częstotliwości (malejąco)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                // 7. Ograniczenie liczby słów do wordsLimit
                .limit(wordsLimit)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (k, v) -> { throw new IllegalStateException(String.format("Błąd! Duplikat klucza %s.", k)); },
                    LinkedHashMap::new
                ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
