import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

//promeni thread, napravi novu klasu i tu stavi lepse izgleda nego da ides preko anonimne
//implementiraj za korisnika
//vidi za server jel sta moze da se uradi sto se tice prosledjivanja poruka


public class Klijent_forma extends JFrame {
    Socket s;
    PrintStream out;
    Scanner in;
    String ime;


    JLabel l1 = new JLabel("Chat:");
    JLabel l2 = new JLabel("Korisnici:");
    JLabel l3 = new JLabel("Poruka:");

    JPanel p1 = new JPanel();
    JTextArea t1 = new JTextArea();
    JScrollPane jsp = new JScrollPane(t1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    JPanel p2 = new JPanel();
    JTextArea t2 = new JTextArea();
    JScrollPane jsp2 = new JScrollPane(t2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    JPanel p3 = new JPanel();
    JTextArea t3 = new JTextArea();
    JScrollPane jsp3 = new JScrollPane(t3, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    JCheckBox muzika = new JCheckBox("Zvuk");
    JCheckBox night = new JCheckBox("Nocni rezim");
    Color stara_boja = getBackground();

    JButton b1 = new JButton("Posalji");


    public void pusti_muziku(String naziv_fajla, boolean pokreni) throws IOException {
        if(pokreni)
        {
            AudioPlayer plejer = AudioPlayer.player;
            plejer.start(new AudioStream(new FileInputStream(naziv_fajla)));
        }
    }


    class Chat_thread extends Thread
    {
        String poruka;
        String[] pomocna;

        public void run()
        {
            while (true)
            {
                poruka = in.nextLine();
                if(poruka.equals("Poruka"))
                {
                    t1.append(in.nextLine() + "\n");
                    try {
                        pusti_muziku("stairs.wav", muzika.isSelected());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(poruka.equals("Korisnici"))
                {
                    pomocna = in.nextLine().split("_");
                    t2.setText("");
                    for(String p : pomocna) t2.append(p + "\n");
                }
                else if(poruka.equals("Gasi")) break;
            }
        }
    }

    Chat_thread za_chat;


    class Pomocna_Forma extends JFrame {
        Klijent_forma glavna;
        JLabel l1 = new JLabel("Ime:");
        JLabel l2 = new JLabel("IP:");
        JLabel l3 = new JLabel("Port:");

        JTextField t1 = new JTextField();
        JTextField t2 = new JTextField();
        JTextField t3 = new JTextField();

        JButton b1 = new JButton("Konektuj se");

        class Nadimak extends Exception{}


        Pomocna_Forma(Klijent_forma g) {
            glavna = g;
            setSize(400, 400);
            setLayout(new GridLayout(7, 1));
            setLocationRelativeTo(null);
            add(l1);
            add(t1);
            add(l2);
            add(t2);
            add(l3);
            add(t3);
            add(b1);
            l1.setHorizontalAlignment(SwingConstants.CENTER);
            l2.setHorizontalAlignment(SwingConstants.CENTER);
            l3.setHorizontalAlignment(SwingConstants.CENTER);
            t1.setHorizontalAlignment(SwingConstants.CENTER);
            t2.setHorizontalAlignment(SwingConstants.CENTER);
            t3.setHorizontalAlignment(SwingConstants.CENTER);
            l1.setFont(l1.getFont().deriveFont(Font.BOLD, 17f));
            l2.setFont(l2.getFont().deriveFont(Font.BOLD, 17f));
            l3.setFont(l3.getFont().deriveFont(Font.BOLD, 17f));
            t1.setFont(t1.getFont().deriveFont(Font.BOLD, 17f));
            t2.setFont(t2.getFont().deriveFont(Font.BOLD, 17f));
            t3.setFont(t3.getFont().deriveFont(Font.BOLD, 17f));
            b1.setFont(b1.getFont().deriveFont(Font.BOLD, 17f));
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);
            b1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                        if(t1.getText().length()<=10)
                        {
                            System.out.println(t1.getText().length());
                            s = new Socket(t2.getText(), Integer.parseInt(t3.getText()));
                            if (s.isConnected()) {
                                ime = t1.getText();
                                out = new PrintStream(s.getOutputStream());
                                in = new Scanner(s.getInputStream());
                                setVisible(false);
                                glavna.setVisible(true);
                                out.println(ime);
                                za_chat = new Chat_thread();
                                za_chat.start();
                            }
                        }
                        else
                        {
                            t1.setText("");
                            t1.requestFocus();
                            throw new Nadimak();
                        }

                    }
                    catch (Nadimak e)
                    {
                        JOptionPane.showMessageDialog(null, "Imate vise od 10 karaktera u imenu");
                    }
                    catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Niste se povezali na server!");
                        e.printStackTrace();
                    }
                }
            });

            getRootPane().setDefaultButton(b1);
        }
    }

    Pomocna_Forma p;

    Klijent_forma() {


        p = new Pomocna_Forma(this);
        setSize(600, 650);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                t1.setText("");
                t2.setText("");
                t3.setText("");
                out.println("Gasi");
                in.close();
                out.close();
                try {
                    s.close();
                    setVisible(false);
                    p.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (t3.getText().length() != 0) {
                    t1.append("Ti:" + t3.getText() + "\n");
                    out.println(t3.getText());
                    t3.setText("");
                }
            }
        });


        Action pritisnuo_dugme = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                b1.doClick();
            }
        };

        t3.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
                "caoo");
        t3.getActionMap().put("caoo",
                pritisnuo_dugme);

        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        add(l1);
        add(l2);
        add(l3);
        p1.setLayout(new BorderLayout(0,0));
        p1.add(jsp, BorderLayout.CENTER);
        p1.setVisible(true);
        add(p1);
        p2.setLayout(new BorderLayout());
        p2.add(jsp2, BorderLayout.CENTER);
        p2.setVisible(true);
        add(p2);
        add(p3);
        p3.setLayout(new BorderLayout());
        p3.add(jsp3, BorderLayout.CENTER);
        p3.setVisible(true);
        add(b1);
        add(muzika);
        add(night);
        l1.setBounds(10, 10, 100, 20); //x y width height
        l2.setBounds(480, 10, 100, 20);
        l1.setFont(l1.getFont().deriveFont(20f));
        l2.setFont(l2.getFont().deriveFont(20f));
        p1.setBounds(10, 40, 440, 400);
        t1.setLineWrap(true);
        t1.setWrapStyleWord(true);
        t1.setFont(t1.getFont().deriveFont(Font.BOLD, 17f));
        t2.setLineWrap(true);
        t2.setWrapStyleWord(true);
        t2.setFont(t1.getFont().deriveFont(Font.BOLD, 17f));
        t1.setBorder(BorderFactory.createLineBorder(Color.black));
        t1.setEditable(false);
        t2.setEditable(false);
        p2.setBounds(461, 40, 125, 400);
        t2.setBorder(BorderFactory.createLineBorder(Color.black));
        l3.setBounds(10, 410, 100, 100);
        l3.setFont(l3.getFont().deriveFont(20f));
        muzika.setBounds(460, 471, 100, 30);
        muzika.setFont(muzika.getFont().deriveFont(20f));
        p3.setBounds(10, 480, 440, 70);
        t3.setBorder(BorderFactory.createLineBorder(Color.black));
        t3.setFont(t1.getFont().deriveFont(Font.BOLD, 17f));
        t3.setLineWrap(true);
        t3.setWrapStyleWord(true);
        b1.setBounds(10, 560, 575, 50);
        b1.setBorder(BorderFactory.createLineBorder(Color.black));
        b1.setFont(b1.getFont().deriveFont(20f));
        night.setBounds(460, 500, 150, 30);
        night.setFont(muzika.getFont().deriveFont(20f));
        menjanje_boje(Color.black);
        b1.setForeground(Color.black);
        t1.setForeground(Color.black);
        t2.setForeground(Color.black);
        t3.setForeground(Color.black);

        t1.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                jsp.getVerticalScrollBar().setValue(t1.getHeight());
            }
        });

        t2.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                jsp2.getVerticalScrollBar().setValue(t2.getHeight());
            }
        });

        t3.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                jsp3.getVerticalScrollBar().setValue(t3.getHeight());
            }
        });

        night.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (night.isSelected()) {
                    Color c = new Color(103, 105, 100);
                    getContentPane().setBackground(Color.black);
                    t1.setBackground(c);
                    t2.setBackground(c);
                    t3.setBackground(c);
                    menjanje_boje(c);
                    night.setBackground(Color.black);
                    muzika.setBackground(Color.black);
                    b1.setBackground(c);

                } else {
                    getContentPane().setBackground(stara_boja);
                    t1.setBackground(Color.white);
                    t2.setBackground(Color.white);
                    t3.setBackground(Color.white);
                    night.setBackground(stara_boja);
                    muzika.setBackground(stara_boja);
                    b1.setBackground(stara_boja);
                    menjanje_boje(Color.black);
                }
            }
        });
    }

    private void menjanje_boje(Color c)
    {
        l1.setForeground(c);
        l2.setForeground(c);
        l3.setForeground(c);
        muzika.setForeground(c);
        night.setForeground(c);
    }


    public static void main(String[] args) {
        Klijent_forma k = new Klijent_forma();
        Klijent_forma b = new Klijent_forma();
        k.p.t1.setText("a");
        k.p.t2.setText("127.0.0.1");
        k.p.t3.setText("9000");
        b.p.t1.setText("b");
        b.p.t2.setText("127.0.0.1");
        b.p.t3.setText("9000");
    }


}
