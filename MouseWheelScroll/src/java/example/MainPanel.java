package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class MainPanel extends JPanel {
    private boolean isPressed = false;
    private final JLabel label = new JLabel();
    private final JScrollPane scroll = new JScrollPane(label);
    private final JScrollBar vBar = scroll.getVerticalScrollBar();
    private final JScrollBar hBar = scroll.getHorizontalScrollBar();
    private final JScrollBar vsb = new JScrollBar(JScrollBar.VERTICAL) {
        @Override public boolean isVisible() {
            if(isPressed) {
                return false;
            }else{
                return super.isVisible();
            }
        }
        @Override public Dimension getPreferredSize() {
            Dimension dim = super.getPreferredSize();
            return new Dimension(0, dim.height);
        }
    };
    private final JScrollBar hsb = new JScrollBar(JScrollBar.HORIZONTAL) {
        @Override public Dimension getPreferredSize() {
            Dimension dim = super.getPreferredSize();
            return new Dimension(dim.width, 0);
        }
    };
    private final JViewport vport    = scroll.getViewport();
    private final MouseAdapter ml = new DragScrollListener();

    private final JRadioButton r0 = new JRadioButton("PreferredSize: 0, shift pressed: Horizontal WheelScrolling");
    private final JRadioButton r1 = new JRadioButton("SCROLLBAR_ALWAYS");
    private final JRadioButton r2 = new JRadioButton("SCROLLBAR_NEVER");

    public MainPanel() {
        super(new BorderLayout());
        // [http://sozai-free.com/ CRW_3857_JFR.jpg]
        label.setIcon(new ImageIcon(getClass().getResource("CRW_3857_JFR.jpg")));
        label.addMouseMotionListener(ml);
        label.addMouseListener(ml);
        for(JScrollBar sb:java.util.Arrays.asList(vsb, hsb, vBar, hBar)) {
            sb.setUnitIncrement(25);
        }

        InputMap im  = scroll.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = scroll.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, InputEvent.SHIFT_MASK, false), "pressed");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, true), "released");
        am.put("pressed", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                isPressed = true;
            }
        });
        am.put("released", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                isPressed = false;
            }
        });

        ActionListener al = new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if(r2.isSelected()) {
                    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                }else{
                    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                    scroll.setVerticalScrollBar(r0.isSelected()?vsb:vBar);
                    scroll.setHorizontalScrollBar(r0.isSelected()?hsb:hBar);
                }
            }
        };
        ButtonGroup bg = new ButtonGroup();
        for(AbstractButton b:java.util.Arrays.asList(r0, r1, r2)) {
            b.addActionListener(al); bg.add(b);
        }
        
        Box b = Box.createHorizontalBox();
        JPanel p = new JPanel(new GridLayout(2,1));
        b.add(r1); b.add(r2);
        p.add(r0); p.add(b);

        r0.setSelected(true);
        scroll.setVerticalScrollBar(vsb);
        scroll.setHorizontalScrollBar(hsb);

        //JScrollBar vsb = scroll.getVerticalScrollBar();
        //vsb.setPreferredSize(new Dimension(0, vsb.getPreferredSize().height));
        //vsb.putClientProperty("JScrollBar.fastWheelScrolling", Boolean.TRUE);
        //JScrollBar hsb = scroll.getHorizontalScrollBar();
        //hsb.setPreferredSize(new Dimension(hsb.getPreferredSize().width, 0));

        add(p, BorderLayout.NORTH);
        add(scroll);
        setPreferredSize(new Dimension(320, 240));
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
class DragScrollListener extends MouseAdapter {
    private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final Point pp = new Point();
    @Override public void mouseDragged(MouseEvent e) {
        final JComponent jc = (JComponent)e.getSource();
        Container c = jc.getParent();
        if(c instanceof JViewport) {
            JViewport vport = (JViewport)c;
            Point cp = SwingUtilities.convertPoint(jc,e.getPoint(),vport);
            Point vp = vport.getViewPosition();
            vp.translate(pp.x-cp.x, pp.y-cp.y);
            jc.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
            pp.setLocation(cp);
        }
    }
    @Override public void mousePressed(MouseEvent e) {
        JComponent jc = (JComponent)e.getSource();
        Container c = jc.getParent();
        if(c instanceof JViewport) {
            jc.setCursor(hndCursor);
            JViewport vport = (JViewport)c;
            Point cp = SwingUtilities.convertPoint(jc,e.getPoint(),vport);
            pp.setLocation(cp);
        }
    }
    @Override public void mouseReleased(MouseEvent e) {
        ((JComponent)e.getSource()).setCursor(defCursor);
    }
}