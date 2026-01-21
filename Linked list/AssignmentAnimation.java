// Assignment for the course BIC2214 Data Structures & Algorithm

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.*;

//Creating logical node
class Node {
    Object data;
    Node next;

    Node(Object data) {
        this.data = data;
    }
}

//Creating visual node (For animations) 
class VisualNode {
    int x, y;
    int targetX;
    String data;

    VisualNode(String data, int par, int par1) {
        this.data = data;
        this.x  = par;
        this.y = par1;
        this.targetX = par;           // Prevent values from stacking/overlapping by constantly updating new value 
    }
}

//Creating drawing panel for animation to occur
class AnimationPanel extends JPanel {
    ArrayList<VisualNode> nodes = new ArrayList<>();

    @Override
    public Dimension getPreferredSize() {
        int width = Math.max(1200, nodes.size() * 100 + 40);
        return new Dimension(width, 150);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < nodes.size(); i++) {
            VisualNode n  = nodes.get(i);

            g.drawRect(n.x, n.y, 80, 40);
            g.drawString(n.data, n.x + 25, n.y + 25);

            if (i < nodes.size() - 1) {
                g.drawLine(n.x + 80, n.y + 20,
                            nodes.get(i+1).x, n.y + 20);
            }
        }
    }
}

// Main GUI Frame
public class AssignmentAnimation extends JFrame {

    private Node head, tail;

    private AnimationPanel panel = new AnimationPanel();

    private JTextField valueField, positionField;

    public AssignmentAnimation() {
        setTitle("Animated Linked List");
        setSize(1200, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();               // Input for values & position
        valueField = new JTextField(10);
        positionField = new JTextField(5);

        inputPanel.add(new JLabel("Value:"));
        inputPanel.add(valueField);
        inputPanel.add(new JLabel("Position:"));
        inputPanel.add(positionField);

        JPanel buttonPanel = new JPanel();              // Creation of buttons

        JButton insertStartBtn = new JButton("Insert at Start");
        JButton insertPosBtn = new JButton("Insert at Position");
        JButton deleteBtn = new JButton("Delete");
        JButton emptyBtn = new JButton("Is empty?");
        JButton dequeueBtn = new JButton("Dequeue");
        JButton peekBtn = new JButton("Peek");
        JButton sizeBtn = new JButton("Size");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");
        JButton testBtn = new JButton("Test");

        buttonPanel.add(insertStartBtn);
        buttonPanel.add(insertPosBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(emptyBtn);
        buttonPanel.add(dequeueBtn);
        buttonPanel.add(peekBtn);
        buttonPanel.add(sizeBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(testBtn);

        panel.setPreferredSize(new Dimension(800, 150));    // Size for GUI
        panel.setBackground(Color.WHITE);                   // BG Color

        add(inputPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(panel);            // Creation of scrollbar (Only horizontal, no vertical)
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        insertStartBtn.addActionListener(e -> insertStart());       // Associating actions for buttons
        insertPosBtn.addActionListener(e -> insertPos());
        deleteBtn.addActionListener(e -> deleteNode());
        emptyBtn.addActionListener(e -> 
                                    JOptionPane.showMessageDialog(this, 
                                    head == null ? "List is empty" : "List is not empty"));
        dequeueBtn.addActionListener(e -> dequeue());
        peekBtn.addActionListener(e -> peek());
        sizeBtn.addActionListener(e -> getTotal());
        saveBtn.addActionListener(e -> saveFile());
        loadBtn.addActionListener(e -> loadFile());
        testBtn.addActionListener(e -> autoTest());
    }

    // Core functions for buttons
    private void getTotal() {               // Get Size 
        int count = 0;
        Node curr = head;
        while (curr != null) {
            count++;
            curr = curr.next;
        }
        JOptionPane.showMessageDialog(this, "Size: " +count);
    }

    private void dequeue() {                // Pop front value
        if (head == null) return;

        head = head.next;
        panel.nodes.remove(0);

        if (head == null) tail = null;

        animateNodes();
    }

    private void peek() {                   // Check front value
        if (head == null) {
            JOptionPane.showMessageDialog(this, "The list is empty");
        }

        else {
            JOptionPane.showMessageDialog(this, "Front value: " +head.data);
        }
    }

    private void insertStart() {            // Insert at Begining
        String value = valueField.getText();
        if (value.isEmpty()) return;

        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;

        VisualNode vn = new VisualNode(value, -100, 50);
        panel.nodes.add(0, vn);

        animateNodes();
    }

    private void insertPos() {              // Insert at specific position (Checking for valid/invalid position)
        String value = valueField.getText();
        if (value.isEmpty()) return;

        int pos;
        try {
            pos = Integer.parseInt(positionField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid position");
            return;
        }

        if (pos == 0 || head == null) {
            insertStart();
            return;
        }

        Node curr = head;
        for (int i = 0; i < pos - 1 && curr != null; i++)
        curr = curr.next;

        if (curr == null) {
            JOptionPane.showMessageDialog(this, "Position out of bounds");
            return;
        }

        Node newNode = new Node(value);
        newNode.next = curr.next;
        curr.next = newNode;

        VisualNode vn = new VisualNode(value, -100, 50);
        panel.nodes.add(pos, vn);

        animateNodes();
    }

    private void deleteNode() {             // Delete element from specific node
        String value = valueField.getText();
        if (head == null) return;

        if (head.data.equals(value)) {
            head = head.next;
            panel.nodes.remove(0);
            animateNodes();
            return;
        }

        Node curr = head;
        int index = 1;

        while (curr.next != null && !curr.next.data.equals(value)) {
            curr = curr.next;
            index++;
        }

        if (curr.next != null) {
            curr.next = curr.next.next;
            panel.nodes.remove(index);
            animateNodes();
        }
    }

    private void enqueueNoAnimation(String value) {
        Node newNode = new Node(value);

        if (head == null) {
            head = newNode;
        }

        else {
            Node curr = head;
            while (curr.next != null) curr = curr.next;
            curr.next = newNode;
        }

        panel.nodes.add(new VisualNode(value, -100, 50));
    }

    private void insertTest(int par) {
                String value = valueField.getText();
        if (value.isEmpty()) return;

        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;

        VisualNode vn = new VisualNode(value, -100, 50);
        panel.nodes.add(0, vn);

        animateNodes();
    }

    private void clearList() {
        head = null;
        panel.nodes.clear();
    }

// Save and Load to/from txt file
    private void saveFile() {
        try (PrintWriter pw = new PrintWriter("linkedlist.txt")) {
            for (VisualNode n : panel.nodes)
                pw.println(n.data);
                JOptionPane.showMessageDialog(this, "Saved to linkedlist.txt");
        } 
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Saved failed");
        }
    }

    private void loadFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("linkedlist.txt"))) {

            head = null;
            panel.nodes.clear();
            
            String line;
            while ((line = br.readLine()) != null) {
                enqueueNoAnimation(line);
    }
            animateNodes();
            JOptionPane.showMessageDialog(this, "Load successfully!"); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Load failed!");
            }
    }

// Performance Test for 100 & 150 insert + delete
    private void autoTest() {
        long sTime, eTime;

        clearList();
        sTime = System.nanoTime();

        for (int i = 0; i < 100; i++) {         // Calculate total time for inserting 100 elements
            insertTest(0 + i);
        }

        eTime = System.nanoTime();
        long t100iTime = eTime - sTime;         // t100iTime = Total 100 elements insert Time

        sTime = System.nanoTime();              // Calculate totla time for deleting 100 elements
        for (int i = 0; i < 100; i++) {
            dequeue();
        }

        eTime = System.nanoTime();
        long t100dTime = eTime - sTime;         // t100dTime = Total 100 elements delete Time

        clearList();

        sTime = System.nanoTime();              // Calculate total time for inserting 150 elements
        for (int i = 0; i < 150; i++) {
            insertTest(0 + i);
        }

        eTime = System.nanoTime();
        long t150iTime = eTime - sTime;

        sTime = System.nanoTime();             // Calculate total time for deleting 150 elements
        for (int i = 0; i < 150; i++) {
            dequeue();
        }

        eTime = System.nanoTime();
        long t150dTime = eTime - sTime;

        JOptionPane.showMessageDialog(this, """
                                            Performance Test Results: 
                                            
                                            Inserting 100 elements: """ +t100iTime + "ns\nDeleting 100 elements: " +t100dTime+ "ns \n" + "Inserting 150 elements: " +t150iTime+ "ns\nDeleting 150 elements: " +t150dTime+ "ns \n", "Performance Test", JOptionPane.INFORMATION_MESSAGE);

    }

    private void animateNodes() {                       // Animation Engine

        for(int i = 0; i < panel.nodes.size(); i++) {
            panel.nodes.get(i).targetX = 20 + i*100;
        }

        Timer timer = new Timer(15, null);

        timer.addActionListener(e -> {
            boolean done = true;

            for (VisualNode n : panel.nodes) {
                if(n.x < n.targetX) {
                    n.x += 5;
                    done = false;
                }

                else if (n.x > n.targetX) {
                    n.x -= 5;
                    done = false;
                }
            }

            panel.revalidate();                     //Revalidate required in order to update scrollbar in real time
            panel.repaint();

            if (done)
                ((Timer) e.getSource()).stop();
        });

        timer.start();
    }

    // Main function
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new AssignmentAnimation().setVisible(true));
    }
}