import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Minimal rules:
 * - Two players alternate turns: RED then BLUE.
 * - Click "Roll Dice" to start/continue a turn. You get 1..6 edges for that turn.
 * - Click two different dots to draw one edge (if it doesn't exist).
 * - If that edge completes any triangle(s), they are scored immediately:
 *   RED triangles = 2 pts each; BLUE triangles = 1 pt each.
 * - Triangles are claimed once; no double scoring.
 */
public class GameController {

    // UI
    @FXML private Pane board;
    @FXML private Button rollBtn;
    @FXML private Label currentPlayerLbl;
    @FXML private Label remainingLbl;
    @FXML private Label scoreRedLbl;
    @FXML private Label scoreBlueLbl;

    // Config
    private static final int DOT_COUNT = 18;
    private static final double DOT_RADIUS = 6;
    private static final double PADDING = 32;
    private static final double MAX_EDGE_LENGTH = 250; // Maximum allowed edge length

    // State
    private enum Player { RED, BLUE }
    private Player current = Player.RED;
    private int edgesRemaining = 0;
    private int scoreRed = 0;
    private int scoreBlue = 0;

    // Graph structures
    private List<Point2D> points = new ArrayList<>();
    private Map<Integer, Circle> dotNodes = new HashMap<>();
    private Set<Edge> edges = new HashSet<>();
    private Set<Tri> claimedTriangles = new HashSet<>();

    // Selection
    private Integer firstSelected = null;

    @FXML
    private void initialize() {
        // Wait for layout to complete before scattering dots
        board.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            if (points.isEmpty() && newVal.getWidth() > 0 && newVal.getHeight() > 0) {
                resetBoard();
            }
        });
    }

    @FXML
    private void onRoll() {
        if (edgesRemaining > 0) return;
        edgesRemaining = rollDice();
        updateHud();
    }

    @FXML
    private void onReset() {
        resetBoard();
    }

    private void resetBoard() {
        board.getChildren().clear();
        points.clear();
        dotNodes.clear();
        edges.clear();
        claimedTriangles.clear();
        firstSelected = null;

        scoreRed = 0;
        scoreBlue = 0;
        current = Player.RED;
        edgesRemaining = 0;

        generateGeometricPattern();
        drawDots();
        updateHud();
    }

    private void generateGeometricPattern() {
        double w = Math.max(300, board.getWidth() == 0 ? 900 : board.getWidth());
        double h = Math.max(200, board.getHeight() == 0 ? 520 : board.getHeight());
        double centerX = w / 2;
        double centerY = h / 2;

        // Randomly select one of 10 patterns
        int pattern = new Random().nextInt(10);

        switch (pattern) {
            case 0: createHexagonalGrid(centerX, centerY); break;
            case 1: createConcentricCircles(centerX, centerY); break;
            case 2: createTriangularGrid(centerX, centerY); break;
            case 3: createSquareGrid(centerX, centerY); break;
            case 4: createStarPattern(centerX, centerY); break;
            case 5: createDiamondPattern(centerX, centerY); break;
            case 6: createSpiral(centerX, centerY); break;
            case 7: createFlowerPattern(centerX, centerY); break;
            case 8: createDoubleHexagon(centerX, centerY); break;
            case 9: createOctagonPattern(centerX, centerY); break;
        }
    }

    private void createHexagonalGrid(double cx, double cy) {
        double spacing = 60;
        for (int row = -2; row <= 2; row++) {
            int cols = 4 - Math.abs(row);
            for (int col = -cols; col <= cols; col++) {
                double x = cx + col * spacing + (row % 2) * spacing / 2;
                double y = cy + row * spacing * 0.866;
                points.add(new Point2D(x, y));
            }
        }
    }

    private void createConcentricCircles(double cx, double cy) {
        points.add(new Point2D(cx, cy)); // center
        int[] dotsPerRing = {6, 8, 10};
        double[] radii = {60, 110, 160};
        
        for (int ring = 0; ring < 3; ring++) {
            int dots = dotsPerRing[ring];
            double radius = radii[ring];
            for (int i = 0; i < dots; i++) {
                double angle = 2 * Math.PI * i / dots;
                double x = cx + radius * Math.cos(angle);
                double y = cy + radius * Math.sin(angle);
                points.add(new Point2D(x, y));
            }
        }
    }

    private void createTriangularGrid(double cx, double cy) {
        double spacing = 65;
        for (int row = 0; row < 5; row++) {
            int dotsInRow = 5 - row;
            for (int col = 0; col < dotsInRow; col++) {
                double x = cx + (col - dotsInRow / 2.0) * spacing + row * spacing / 2;
                double y = cy - 100 + row * spacing * 0.866;
                points.add(new Point2D(x, y));
            }
        }
    }

    private void createSquareGrid(double cx, double cy) {
        double spacing = 70;
        for (int row = -2; row <= 2; row++) {
            for (int col = -2; col <= 2; col++) {
                double x = cx + col * spacing;
                double y = cy + row * spacing;
                points.add(new Point2D(x, y));
            }
        }
    }

    private void createStarPattern(double cx, double cy) {
        points.add(new Point2D(cx, cy)); // center
        
        // Inner star points
        for (int i = 0; i < 5; i++) {
            double angle = -Math.PI / 2 + 2 * Math.PI * i / 5;
            double x = cx + 60 * Math.cos(angle);
            double y = cy + 60 * Math.sin(angle);
            points.add(new Point2D(x, y));
        }
        
        // Outer star points
        for (int i = 0; i < 5; i++) {
            double angle = -Math.PI / 2 + 2 * Math.PI * i / 5 + Math.PI / 5;
            double x = cx + 120 * Math.cos(angle);
            double y = cy + 120 * Math.sin(angle);
            points.add(new Point2D(x, y));
        }
        
        // Far outer points
        for (int i = 0; i < 5; i++) {
            double angle = -Math.PI / 2 + 2 * Math.PI * i / 5;
            double x = cx + 170 * Math.cos(angle);
            double y = cy + 170 * Math.sin(angle);
            points.add(new Point2D(x, y));
        }
    }

    private void createDiamondPattern(double cx, double cy) {
        // Center
        points.add(new Point2D(cx, cy));
        
        // Diamond layers
        int[][] offsets = {
            {0, -60}, {60, 0}, {0, 60}, {-60, 0},
            {0, -120}, {80, -60}, {120, 0}, {80, 60}, {0, 120}, {-80, 60}, {-120, 0}, {-80, -60}
        };
        
        for (int[] offset : offsets) {
            points.add(new Point2D(cx + offset[0], cy + offset[1]));
        }
    }

    private void createSpiral(double cx, double cy) {
        points.add(new Point2D(cx, cy));
        double angle = 0;
        double radius = 0;
        
        for (int i = 0; i < 20; i++) {
            angle += 0.8;
            radius += 8;
            double x = cx + radius * Math.cos(angle);
            double y = cy + radius * Math.sin(angle);
            points.add(new Point2D(x, y));
        }
    }

    private void createFlowerPattern(double cx, double cy) {
        points.add(new Point2D(cx, cy)); // center
        
        // 6 petals
        for (int petal = 0; petal < 6; petal++) {
            double baseAngle = Math.PI * petal / 3;
            
            // 3 dots per petal
            for (int i = 1; i <= 3; i++) {
                double radius = i * 50;
                double x = cx + radius * Math.cos(baseAngle);
                double y = cy + radius * Math.sin(baseAngle);
                points.add(new Point2D(x, y));
            }
        }
    }

    private void createDoubleHexagon(double cx, double cy) {
        // Inner hexagon
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3;
            double x = cx + 60 * Math.cos(angle);
            double y = cy + 60 * Math.sin(angle);
            points.add(new Point2D(x, y));
        }
        
        // Outer hexagon
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3;
            double x = cx + 140 * Math.cos(angle);
            double y = cy + 140 * Math.sin(angle);
            points.add(new Point2D(x, y));
        }
        
        // Mid points between inner and outer
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3 + Math.PI / 6;
            double x = cx + 100 * Math.cos(angle);
            double y = cy + 100 * Math.sin(angle);
            points.add(new Point2D(x, y));
        }
    }

    private void createOctagonPattern(double cx, double cy) {
        points.add(new Point2D(cx, cy)); // center
        
        // Inner octagon
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4;
            double x = cx + 70 * Math.cos(angle);
            double y = cy + 70 * Math.sin(angle);
            points.add(new Point2D(x, y));
        }
        
        // Outer octagon
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4;
            double x = cx + 140 * Math.cos(angle);
            double y = cy + 140 * Math.sin(angle);
            points.add(new Point2D(x, y));
        }
    }

    private void drawDots() {
        for (int i = 0; i < points.size(); i++) {
            Point2D p = points.get(i);
            Circle c = new Circle(p.getX(), p.getY(), DOT_RADIUS);
            c.getStyleClass().add("dot");
            final int idx = i;
            c.setOnMouseClicked(e -> onDotClicked(idx));
            dotNodes.put(i, c);
            board.getChildren().add(c);
        }
    }

    private void onDotClicked(int idx) {
        if (edgesRemaining <= 0) return;
        
        if (firstSelected == null) {
            firstSelected = idx;
            markSelected(idx, true);
            return;
        }
        
        if (Objects.equals(firstSelected, idx)) {
            markSelected(idx, false);
            firstSelected = null;
            return;
        }

        int a = firstSelected;
        int b = idx;
        markSelected(a, false);

        Edge e = new Edge(a, b);
        if (edges.contains(e)) {
            firstSelected = null;
            return;
        }

        // Check if edge is too long
        Point2D p1 = points.get(a);
        Point2D p2 = points.get(b);
        double distance = p1.distance(p2);
        if (distance > MAX_EDGE_LENGTH) {
            System.out.println("Edge too long: " + distance);
            firstSelected = null;
            return;
        }

        // Check if new edge intersects with any existing edge
        if (intersectsWithAnyEdge(e)) {
            System.out.println("Edge intersects with existing edge");
            firstSelected = null;
            return;
        }

        // Check if new edge passes through any other dot
        if (edgePassesThroughDot(e)) {
            System.out.println("Edge passes through another dot");
            firstSelected = null;
            return;
        }

        drawEdge(e, current);
        edges.add(e);

        claimTrianglesByNewEdge(e, current);

        edgesRemaining = Math.max(0, edgesRemaining - 1);

        // Keep the second dot selected if there are remaining edges
        if (edgesRemaining > 0) {
            firstSelected = b;
            markSelected(b, true);
        } else {
            firstSelected = null;
            current = (current == Player.RED) ? Player.BLUE : Player.RED;
        }

        updateHud();
    }

    private void drawEdge(Edge e, Player p) {
        Point2D p1 = points.get(e.u);
        Point2D p2 = points.get(e.v);
        Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        line.setMouseTransparent(true);
        line.getStyleClass().add(p == Player.RED ? "edge-red" : "edge-blue");
        board.getChildren().add(line);
        sendDotsToFront();
    }

    private void sendDotsToFront() {
        for (Circle c : dotNodes.values()) {
            c.toFront();
        }
    }

    private int claimTrianglesByNewEdge(Edge newEdge, Player p) {
        int gained = 0;
        int a = newEdge.u, b = newEdge.v;

        for (int k = 0; k < points.size(); k++) {
            if (k == a || k == b) continue;
            Edge e1 = new Edge(a, k);
            Edge e2 = new Edge(b, k);
            if (edges.contains(e1) && edges.contains(e2)) {
                Tri t = new Tri(a, b, k);
                if (!claimedTriangles.contains(t)) {
                    claimedTriangles.add(t);
                    drawTriangle(t, p);
                    if (p == Player.RED) scoreRed += 2; 
                    else scoreBlue += 1;
                    gained++;
                }
            }
        }
        return gained;
    }

    private void drawTriangle(Tri t, Player p) {
        Point2D p1 = points.get(t.a);
        Point2D p2 = points.get(t.b);
        Point2D p3 = points.get(t.c);
        Polygon poly = new Polygon(
                p1.getX(), p1.getY(),
                p2.getX(), p2.getY(),
                p3.getX(), p3.getY()
        );
        poly.setMouseTransparent(true);
        poly.getStyleClass().add(p == Player.RED ? "tri-fill-red" : "tri-fill-blue");
        board.getChildren().add(0, poly);
        sendEdgesAboveTriangles();
        sendDotsToFront();
    }

    private void sendEdgesAboveTriangles() {
        List<Node> lines = board.getChildren().stream()
                .filter(n -> n instanceof Line)
                .collect(Collectors.toList());
        for (Node n : lines) n.toFront();
    }

    private void markSelected(int idx, boolean sel) {
        Circle c = dotNodes.get(idx);
        if (c == null) return;
        if (sel) {
            if (!c.getStyleClass().contains("selected")) 
                c.getStyleClass().add("selected");
        } else {
            c.getStyleClass().remove("selected");
        }
    }

    private void updateHud() {
        currentPlayerLbl.setText(current == Player.RED ? "RED" : "BLUE");
        currentPlayerLbl.setTextFill(current == Player.RED ? 
                Color.web("#e53935") : Color.web("#1e88e5"));
        remainingLbl.setText(String.valueOf(edgesRemaining));
        scoreRedLbl.setText(String.valueOf(scoreRed));
        scoreBlueLbl.setText(String.valueOf(scoreBlue));
    }

    private int rollDice() {
        return 1 + new Random().nextInt(6);
    }

    // Check if edge passes through any other dot's perimeter
    private boolean edgePassesThroughDot(Edge edge) {
        Point2D p1 = points.get(edge.u);
        Point2D p2 = points.get(edge.v);

        for (int i = 0; i < points.size(); i++) {
            // Skip the endpoints of the edge
            if (i == edge.u || i == edge.v) {
                continue;
            }

            Point2D dotCenter = points.get(i);
            
            // Calculate the distance from the dot center to the line segment
            double distance = distanceFromPointToLineSegment(dotCenter, p1, p2);
            
            // If the distance is less than or equal to the dot radius, the edge passes through the dot
            if (distance <= DOT_RADIUS) {
                return true;
            }
        }
        return false;
    }

    // Calculate the shortest distance from a point to a line segment
    private double distanceFromPointToLineSegment(Point2D point, Point2D lineStart, Point2D lineEnd) {
        double x0 = point.getX();
        double y0 = point.getY();
        double x1 = lineStart.getX();
        double y1 = lineStart.getY();
        double x2 = lineEnd.getX();
        double y2 = lineEnd.getY();

        double dx = x2 - x1;
        double dy = y2 - y1;
        
        // If the line segment is actually a point
        if (dx == 0 && dy == 0) {
            return Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
        }

        // Calculate the parameter t that represents the projection of the point onto the line
        double t = ((x0 - x1) * dx + (y0 - y1) * dy) / (dx * dx + dy * dy);
        
        // Clamp t to [0, 1] to handle the segment (not the infinite line)
        t = Math.max(0, Math.min(1, t));
        
        // Calculate the closest point on the segment
        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;
        
        // Return the distance from the point to the closest point on the segment
        return Math.sqrt((x0 - closestX) * (x0 - closestX) + (y0 - closestY) * (y0 - closestY));
    }

    // Check if new edge intersects with any existing edge
    private boolean intersectsWithAnyEdge(Edge newEdge) {
        Point2D p1 = points.get(newEdge.u);
        Point2D p2 = points.get(newEdge.v);

        for (Edge existingEdge : edges) {
            // Skip if edges share a common vertex (they can touch at endpoints)
            if (newEdge.u == existingEdge.u || newEdge.u == existingEdge.v ||
                newEdge.v == existingEdge.u || newEdge.v == existingEdge.v) {
                continue;
            }

            Point2D p3 = points.get(existingEdge.u);
            Point2D p4 = points.get(existingEdge.v);

            // Check if the two line segments intersect
            if (lineSegmentsIntersect(p1, p2, p3, p4)) {
                return true;
            }
        }
        return false;
    }

    // Check if two line segments intersect (excluding endpoints)
    private boolean lineSegmentsIntersect(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        double d1 = direction(p3, p4, p1);
        double d2 = direction(p3, p4, p2);
        double d3 = direction(p1, p2, p3);
        double d4 = direction(p1, p2, p4);

        if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) &&
            ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) {
            return true;
        }

        return false;
    }

    // Calculate direction/orientation
    private double direction(Point2D p1, Point2D p2, Point2D p3) {
        return (p3.getX() - p1.getX()) * (p2.getY() - p1.getY()) -
               (p2.getX() - p1.getX()) * (p3.getY() - p1.getY());
    }



    private static final class Edge {
        final int u, v;
        Edge(int a, int b) {
            if (a == b) throw new IllegalArgumentException("self-edge");
            if (a < b) { this.u = a; this.v = b; }
            else { this.u = b; this.v = a; }
        }
        @Override 
        public boolean equals(Object o) {
            if (!(o instanceof Edge)) return false;
            Edge e = (Edge) o;
            return u == e.u && v == e.v;
        }
        @Override 
        public int hashCode() {
            return Objects.hash(u, v);
        }
    }

    private static final class Tri {
        final int a, b, c;
        Tri(int x, int y, int z) {
            int[] arr = new int[]{x, y, z};
            Arrays.sort(arr);
            a = arr[0]; 
            b = arr[1]; 
            c = arr[2];
        }
        @Override 
        public boolean equals(Object o) {
            if (!(o instanceof Tri)) return false;
            Tri t = (Tri) o;
            return a == t.a && b == t.b && c == t.c;
        }
        @Override 
        public int hashCode() {
            return Objects.hash(a, b, c);
        }
    }
}