
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class ClosestPairAnalysis extends JFrame {

    private static final Color BG = new Color(15, 23, 42);
    private static final Color PANEL = new Color(30, 41, 59);
    private static final Color BLUE = new Color(59, 130, 246);
    private static final Color GREEN = new Color(34, 197, 94);
    private static final Color ORANGE = new Color(249, 115, 22);
    private static final Color WHITE = new Color(241, 245, 249);
    private static final Color MUTED = new Color(148, 163, 184);

    private JTextField countField;
    private JLabel statusLabel;
    private JLabel resultLabel;
    private JLabel timeLabel;

    private PointPanel pointPanel;
    private PerformancePanel performancePanel;

    private List<Point2D.Double> points = new ArrayList<>();
    private Point2D.Double closestA;
    private Point2D.Double closestB;
    private double closestDistance = Double.MAX_VALUE;

    private long bruteTime;
    private long divideTime;

    public ClosestPairAnalysis() {
        setTitle("Closest Pair of Points | Algorithm Studio");
        setSize(1250, 850);
        setMinimumSize(new Dimension(1000, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(12, 12));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));

        JLabel title = new JLabel("Closest Pair of Points");
        title.setForeground(WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 27));

        JLabel subtitle = new JLabel(
                "BRUTE FORCE O(N²)   |   DIVIDE AND CONQUER O(N log N)"
        );
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BG);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // CONTROL BAR
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBackground(PANEL);
        controls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        firstRow.setBackground(PANEL);

        JLabel countLabel = new JLabel("Number of points:");
        countLabel.setForeground(WHITE);
        countLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        countField = new JTextField("100", 5);
        countField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        countField.setBackground(BG);
        countField.setForeground(WHITE);
        countField.setCaretColor(WHITE);
        countField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105)),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)
        ));
        countField.setPreferredSize(new Dimension(75, 38));

        JButton generateButton = createButton("Generate Points", BLUE);
        JButton analyzeButton = createButton("Analyze Closest Pair", GREEN);
        JButton performanceButton = createButton("Run Performance Test", ORANGE);

        firstRow.add(countLabel);
        firstRow.add(countField);
        firstRow.add(generateButton);
        firstRow.add(analyzeButton);

        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        secondRow.setBackground(PANEL);
        secondRow.add(performanceButton);

        controls.add(firstRow);
        controls.add(secondRow);

        generateButton.addActionListener(e -> generatePoints());
        analyzeButton.addActionListener(e -> analyzePoints());
        performanceButton.addActionListener(e -> runPerformanceTest());

        // MAIN CONTENT
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(0, 22, 12, 22));

        pointPanel = new PointPanel();
        pointPanel.setPreferredSize(new Dimension(720, 520));

        JPanel visualizationCard = createCard(
                "POINT VISUALIZATION", pointPanel
        );

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(BG);
        rightPanel.setPreferredSize(new Dimension(340, 500));

        resultLabel = new JLabel(
                "<html><div style='width:260px'>Generate points and analyze them to find the closest pair.</div></html>"
        );
        resultLabel.setForeground(WHITE);
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        timeLabel = new JLabel(
                "<html><div style='width:260px'>Execution times will appear here.</div></html>"
        );
        timeLabel.setForeground(MUTED);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel resultsContent = new JPanel();
        resultsContent.setLayout(new BoxLayout(resultsContent, BoxLayout.Y_AXIS));
        resultsContent.setBackground(PANEL);
        resultsContent.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        resultsContent.add(resultLabel);
        resultsContent.add(Box.createVerticalStrut(16));
        resultsContent.add(timeLabel);

        rightPanel.add(createCard("ANALYSIS RESULTS", resultsContent));
        rightPanel.add(Box.createVerticalStrut(12));

        performancePanel = new PerformancePanel();
        performancePanel.setPreferredSize(new Dimension(320, 300));
        performancePanel.setMinimumSize(new Dimension(280, 250));

        rightPanel.add(createCard("PERFORMANCE COMPARISON", performancePanel));

        content.add(visualizationCard, BorderLayout.CENTER);
        content.add(rightPanel, BorderLayout.EAST);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
        centerContainer.setBackground(BG);
        centerContainer.add(controls, BorderLayout.NORTH);
        centerContainer.add(content, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready. Generate points to begin.");
        statusLabel.setForeground(MUTED);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 22, 10, 22));
        add(statusLabel, BorderLayout.SOUTH);

        generatePoints();
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(8, 12, 8, 12));
        button.setPreferredSize(new Dimension(180, 38));
        button.setMinimumSize(new Dimension(165, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createCard(String heading, JComponent component) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel label = new JLabel(heading);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));

        card.add(label, BorderLayout.NORTH);
        card.add(component, BorderLayout.CENTER);
        return card;
    }

    // GENERATE RANDOM POINTS
    private void generatePoints() {
        int n;

        try {
            n = Integer.parseInt(countField.getText().trim());

            if (n < 2 || n > 5000) {
                JOptionPane.showMessageDialog(
                        this,
                        "Enter a number between 2 and 5000.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid whole number.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Random random = new Random();
        points = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            points.add(new Point2D.Double(
                    20 + random.nextDouble() * 960,
                    20 + random.nextDouble() * 560
            ));
        }

        closestA = null;
        closestB = null;
        closestDistance = Double.MAX_VALUE;

        resultLabel.setText(
                "<html><div style='width:260px'>Generated " + n
                        + " random points.<br><br>"
                        + "Click Analyze Closest Pair to find the closest pair.</div></html>"
        );

        timeLabel.setText(
                "<html><div style='width:260px'>Execution times will appear here.</div></html>"
        );

        statusLabel.setText("Generated " + n + " points.");
        pointPanel.repaint();
        performancePanel.repaint();
    }

    // ANALYZE USING BOTH ALGORITHMS
    private void analyzePoints() {
        if (points.size() < 2) {
            statusLabel.setText("Generate at least two points first.");
            return;
        }

        List<Point2D.Double> copy = new ArrayList<>(points);

        long start = System.nanoTime();
        PairResult bruteResult = bruteForce(copy);
        bruteTime = System.nanoTime() - start;

        List<Point2D.Double> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.comparingDouble(p -> p.x));

        start = System.nanoTime();
        PairResult divideResult = divideAndConquer(sorted);
        divideTime = System.nanoTime() - start;

        closestA = divideResult.a;
        closestB = divideResult.b;
        closestDistance = divideResult.distance;

        resultLabel.setText(
                "<html><div style='width:260px'>"
                        + "<b>Closest Pair Found</b><br><br>"
                        + "Point A: (" + format(closestA.x) + ", "
                        + format(closestA.y) + ")<br><br>"
                        + "Point B: (" + format(closestB.x) + ", "
                        + format(closestB.y) + ")<br><br>"
                        + "Distance: <b>" + format(closestDistance) + "</b>"
                        + "</div></html>"
        );

        timeLabel.setText(
                "<html><div style='width:260px'>"
                        + "Brute Force O(N²):<br><b>"
                        + formatTime(bruteTime) + "</b><br><br>"
                        + "Divide & Conquer O(N log N):<br><b>"
                        + formatTime(divideTime) + "</b>"
                        + "</div></html>"
        );

        statusLabel.setText(
                "Analysis complete. Brute force: " + formatTime(bruteTime)
                        + " | Divide & conquer: " + formatTime(divideTime)
        );

        performancePanel.setSingleRun(bruteTime, divideTime);
        pointPanel.repaint();
        performancePanel.repaint();
    }

    // BRUTE FORCE ALGORITHM
    private PairResult bruteForce(List<Point2D.Double> pts) {
        double minDistance = Double.MAX_VALUE;
        Point2D.Double bestA = null;
        Point2D.Double bestB = null;

        for (int i = 0; i < pts.size(); i++) {
            for (int j = i + 1; j < pts.size(); j++) {
                double d = distance(pts.get(i), pts.get(j));

                if (d < minDistance) {
                    minDistance = d;
                    bestA = pts.get(i);
                    bestB = pts.get(j);
                }
            }
        }

        return new PairResult(bestA, bestB, minDistance);
    }

    // DIVIDE AND CONQUER ALGORITHM
    private PairResult divideAndConquer(List<Point2D.Double> pts) {
        int n = pts.size();

        if (n <= 3) {
            return bruteForce(pts);
        }

        int mid = n / 2;
        double midX = pts.get(mid).x;

        List<Point2D.Double> left =
                new ArrayList<>(pts.subList(0, mid));
        List<Point2D.Double> right =
                new ArrayList<>(pts.subList(mid, n));

        PairResult leftResult = divideAndConquer(left);
        PairResult rightResult = divideAndConquer(right);

        PairResult best = leftResult.distance < rightResult.distance
                ? leftResult : rightResult;

        double minDistance = best.distance;

        List<Point2D.Double> strip = new ArrayList<>();

        for (Point2D.Double p : pts) {
            if (Math.abs(p.x - midX) < minDistance) {
                strip.add(p);
            }
        }

        strip.sort(Comparator.comparingDouble(p -> p.y));

        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1;
                 j < strip.size()
                         && strip.get(j).y - strip.get(i).y < minDistance;
                 j++) {

                double d = distance(strip.get(i), strip.get(j));

                if (d < minDistance) {
                    minDistance = d;
                    best = new PairResult(
                            strip.get(i),
                            strip.get(j),
                            minDistance
                    );
                }
            }
        }

        return best;
    }

    private double distance(Point2D.Double a, Point2D.Double b) {
        return Math.hypot(a.x - b.x, a.y - b.y);
    }

    private String format(double value) {
        return String.format(Locale.US, "%.3f", value);
    }

    private String formatTime(long nanos) {
        if (nanos < 1_000_000) {
            return String.format(Locale.US, "%.3f µs", nanos / 1000.0);
        }
        return String.format(Locale.US, "%.3f ms", nanos / 1_000_000.0);
    }

    // PERFORMANCE TEST
    private void runPerformanceTest() {
        statusLabel.setText("Running performance test...");
        performancePanel.setTesting(true);
        performancePanel.repaint();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private final List<Integer> sizes = Arrays.asList(
                    50, 100, 200, 400, 600, 800, 1000
            );

            private final List<Long> bruteTimes = new ArrayList<>();
            private final List<Long> divideTimes = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                Random random = new Random(42);

                for (int size : sizes) {
                    List<Point2D.Double> testPoints = new ArrayList<>();

                    for (int i = 0; i < size; i++) {
                        testPoints.add(new Point2D.Double(
                                random.nextDouble() * 1000,
                                random.nextDouble() * 1000
                        ));
                    }

                    long start = System.nanoTime();
                    bruteForce(testPoints);
                    long brute = System.nanoTime() - start;

                    List<Point2D.Double> sorted =
                            new ArrayList<>(testPoints);
                    sorted.sort(Comparator.comparingDouble(p -> p.x));

                    start = System.nanoTime();
                    divideAndConquer(sorted);
                    long divide = System.nanoTime() - start;

                    bruteTimes.add(brute);
                    divideTimes.add(divide);
                }

                return null;
            }

            @Override
            protected void done() {
                performancePanel.setData(
                        sizes, bruteTimes, divideTimes
                );
                performancePanel.setTesting(false);
                performancePanel.repaint();
                statusLabel.setText("Performance test completed.");
            }
        };

        worker.execute();
    }

    // PAIR RESULT CLASS
    private static class PairResult {
        Point2D.Double a;
        Point2D.Double b;
        double distance;

        PairResult(
                Point2D.Double a,
                Point2D.Double b,
                double distance
        ) {
            this.a = a;
            this.b = b;
            this.distance = distance;
        }
    }

    // POINT VISUALIZATION PANEL
    private class PointPanel extends JPanel {

        PointPanel() {
            setBackground(BG);
            setBorder(BorderFactory.createLineBorder(
                    new Color(51, 65, 85)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            // Grid
            g2.setColor(PANEL);

            for (int x = 0; x < width; x += 30) {
                g2.drawLine(x, 0, x, height);
            }

            for (int y = 0; y < height; y += 30) {
                g2.drawLine(0, y, width, y);
            }

            if (points.isEmpty()) {
                g2.dispose();
                return;
            }

            double minX = points.stream()
                    .mapToDouble(p -> p.x).min().orElse(0);
            double maxX = points.stream()
                    .mapToDouble(p -> p.x).max().orElse(1);
            double minY = points.stream()
                    .mapToDouble(p -> p.y).min().orElse(0);
            double maxY = points.stream()
                    .mapToDouble(p -> p.y).max().orElse(1);

            double scaleX = (width - 40) / Math.max(1, maxX - minX);
            double scaleY = (height - 40) / Math.max(1, maxY - minY);
            double scale = Math.min(scaleX, scaleY);

            double drawWidth = (maxX - minX) * scale;
            double drawHeight = (maxY - minY) * scale;
            double offsetX = (width - drawWidth) / 2;
            double offsetY = (height - drawHeight) / 2;

            // Divide line
            if (points.size() > 3) {
                List<Point2D.Double> sorted = new ArrayList<>(points);
                sorted.sort(Comparator.comparingDouble(p -> p.x));

                double midX = sorted.get(sorted.size() / 2).x;
                int screenX = (int) (
                        offsetX + (midX - minX) * scale
                );

                g2.setColor(new Color(249, 115, 22, 180));
                g2.setStroke(new BasicStroke(
                        2f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10,
                        new float[]{7, 6},
                        0
                ));
                g2.drawLine(screenX, 0, screenX, height);
            }

            // Closest-pair strip
            if (closestA != null && closestB != null) {
                double leftX = Math.min(closestA.x, closestB.x);
                double rightX = Math.max(closestA.x, closestB.x);

                int sx1 = (int) (
                        offsetX + (leftX - minX) * scale
                );
                int sx2 = (int) (
                        offsetX + (rightX - minX) * scale
                );

                g2.setColor(new Color(34, 197, 94, 35));
                g2.fillRect(
                        Math.min(sx1, sx2) - 12,
                        0,
                        Math.max(24, Math.abs(sx2 - sx1) + 24),
                        height
                );
            }

            // LARGER POINTS
            for (Point2D.Double p : points) {
                int x = (int) (
                        offsetX + (p.x - minX) * scale
                );
                int y = (int) (
                        height - (offsetY + (p.y - minY) * scale)
                );

                // Outer glow
                g2.setColor(new Color(59, 130, 246, 80));
                g2.fillOval(x - 9, y - 9, 18, 18);

                // Main point
                g2.setColor(BLUE);
                g2.fillOval(x - 5, y - 5, 10, 10);

                // Center
                g2.setColor(WHITE);
                g2.fillOval(x - 2, y - 2, 4, 4);
            }

            // HIGHLIGHT CLOSEST PAIR
            if (closestA != null && closestB != null) {
                int x1 = (int) (
                        offsetX + (closestA.x - minX) * scale
                );
                int y1 = (int) (
                        height - (offsetY + (closestA.y - minY) * scale)
                );

                int x2 = (int) (
                        offsetX + (closestB.x - minX) * scale
                );
                int y2 = (int) (
                        height - (offsetY + (closestB.y - minY) * scale)
                );

                g2.setColor(GREEN);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(x1, y1, x2, y2);

                // Larger highlighted points
                g2.fillOval(x1 - 10, y1 - 10, 20, 20);
                g2.fillOval(x2 - 10, y2 - 10, 20, 20);

                g2.setColor(WHITE);
                g2.fillOval(x1 - 3, y1 - 3, 6, 6);
                g2.fillOval(x2 - 3, y2 - 3, 6, 6);

                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString("A", x1 + 12, y1 - 10);
                g2.drawString("B", x2 + 12, y2 - 10);
            }

            g2.dispose();
        }
    }

    // PERFORMANCE GRAPH PANEL
    private class PerformancePanel extends JPanel {

        private List<Integer> sizes = new ArrayList<>();
        private List<Long> bruteTimes = new ArrayList<>();
        private List<Long> divideTimes = new ArrayList<>();
        private boolean testing = false;
        private boolean singleRun = false;

        PerformancePanel() {
            setBackground(PANEL);
            setPreferredSize(new Dimension(320, 300));
            setMinimumSize(new Dimension(280, 250));
        }

        void setSingleRun(long brute, long divide) {
            singleRun = true;
            sizes = new ArrayList<>();
            bruteTimes = new ArrayList<>();
            divideTimes = new ArrayList<>();

            bruteTimes.add(brute);
            divideTimes.add(divide);
            testing = false;
        }

        void setData(
                List<Integer> sizes,
                List<Long> bruteTimes,
                List<Long> divideTimes
        ) {
            this.sizes = new ArrayList<>(sizes);
            this.bruteTimes = new ArrayList<>(bruteTimes);
            this.divideTimes = new ArrayList<>(divideTimes);
            singleRun = false;
        }

        void setTesting(boolean testing) {
            this.testing = testing;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            if (testing) {
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString(
                        "Running performance test...",
                        15,
                        height / 2
                );
                g2.dispose();
                return;
            }

            if (bruteTimes.isEmpty() || divideTimes.isEmpty()) {
                g2.setColor(MUTED);
                g2.drawString("Run performance test", 15, height / 2 - 5);
                g2.drawString("to compare both algorithms.", 15, height / 2 + 15);
                g2.dispose();
                return;
            }

            int left = 42;
            int right = 12;
            int top = 32;
            int bottom = 38;

            int graphWidth = width - left - right;
            int graphHeight = height - top - bottom;

            if (graphWidth <= 0 || graphHeight <= 0) {
                g2.dispose();
                return;
            }

            long maxTime = 1;

            for (long value : bruteTimes) {
                maxTime = Math.max(maxTime, value);
            }

            for (long value : divideTimes) {
                maxTime = Math.max(maxTime, value);
            }

            // Graph grid and axis labels
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));

            for (int i = 0; i <= 4; i++) {
                int y = top + graphHeight - i * graphHeight / 4;

                g2.setColor(new Color(51, 65, 85));
                g2.drawLine(left, y, width - right, y);

                g2.setColor(MUTED);
                String label = String.format(
                        Locale.US,
                        "%.1f",
                        (maxTime / 1_000_000.0) * i / 4.0
                );
                g2.drawString(label, 2, y + 4);
            }

            int count = bruteTimes.size();

            if (singleRun) {
                int barWidth = Math.min(45, graphWidth / 4);
                int gap = 18;
                int total = barWidth * 2 + gap;
                int startX = left + (graphWidth - total) / 2;

                drawBar(
                        g2, startX, top, graphHeight,
                        barWidth, bruteTimes.get(0), maxTime, BLUE
                );

                drawBar(
                        g2, startX + barWidth + gap, top,
                        graphHeight, barWidth,
                        divideTimes.get(0), maxTime, GREEN
                );

                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(MUTED);
                g2.drawString(
                        "Brute",
                        startX + 2,
                        height - 18
                );
                g2.drawString(
                        "Divide",
                        startX + barWidth + gap,
                        height - 18
                );
            } else {
                int groupWidth = Math.max(
                        1, graphWidth / Math.max(1, count)
                );
                int barWidth = Math.max(
                        4, Math.min(18, groupWidth / 3)
                );

                for (int i = 0; i < count; i++) {
                    int centerX =
                            left + i * groupWidth + groupWidth / 2;

                    drawBar(
                            g2,
                            centerX - barWidth - 1,
                            top,
                            graphHeight,
                            barWidth,
                            bruteTimes.get(i),
                            maxTime,
                            BLUE
                    );

                    drawBar(
                            g2,
                            centerX + 1,
                            top,
                            graphHeight,
                            barWidth,
                            divideTimes.get(i),
                            maxTime,
                            GREEN
                    );

                    g2.setColor(MUTED);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    g2.drawString(
                            String.valueOf(sizes.get(i)),
                            centerX - 10,
                            height - 18
                    );
                }
            }

            // Legend
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));

            g2.setColor(BLUE);
            g2.fillRect(left, 9, 10, 10);
            g2.drawString("Brute Force", left + 14, 18);

            g2.setColor(GREEN);
            g2.fillRect(left + 105, 9, 10, 10);
            g2.drawString("Divide & Conquer", left + 119, 18);

            g2.dispose();
        }

        private void drawBar(
                Graphics2D g2,
                int x,
                int top,
                int graphHeight,
                int barWidth,
                long time,
                long maxTime,
                Color color
        ) {
            int barHeight = (int) (
                    ((double) time / maxTime) * graphHeight
            );

            barHeight = Math.max(2, barHeight);

            g2.setColor(color);
            g2.fillRoundRect(
                    x,
                    top + graphHeight - barHeight,
                    barWidth,
                    barHeight,
                    6,
                    6
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            new ClosestPairAnalysis().setVisible(true);
        });
    }
}
