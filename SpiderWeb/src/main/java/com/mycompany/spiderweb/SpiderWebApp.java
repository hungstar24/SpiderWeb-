package com.mycompany.spiderweb;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;

import static com.mycompany.spiderweb.Models.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpiderWebApp extends JFrame {

    private final SettingsStore settings = new SettingsStore();
    private final CseClient client = new CseClient(settings);

    private final DefaultListModel<DomainItem> domainModel = new DefaultListModel<>();
    private final JList<DomainItem> domainList = new JList<>(domainModel);

    private final DefaultListModel<ResultItem> resultModel = new DefaultListModel<>();
    private final JList<ResultItem> resultList = new JList<>(resultModel);

    private final JTextField tfK1 = new JTextField();
    private final JTextField tfK2 = new JTextField();
    private final JTextField tfK3 = new JTextField();

    private final JButton btnSearch = new JButton("🔎 Tìm kiếm");
    private final JButton btnPrice  = new JButton("💰 Tìm giá sản phẩm");
    private final JButton btnPrev   = new JButton("← Prev");
    private final JButton btnNext   = new JButton("Next →");
    private final JButton btnOpen   = new JButton("Mở link");
    private final JButton btnCopy   = new JButton("Copy link");
    private final JButton btnSettings = new JButton("⚙ API");

    private final JLabel lbStatus = new JLabel("Sẵn sàng");
    private final JLabel lbMeta   = new JLabel(" ");
    private final JProgressBar progress = new JProgressBar();

    private List<String> lastKeywords = List.of();
    private List<ResultItem> allResults = List.of();
    private String domainFilter = "Tất cả";

    private final int pageSize = 10;
    private int startIndex = 1;
    private long totalResults = 0;
    private String lastQuery = "";

    public SpiderWebApp() {
        super("SpiderWeb - Tìm kiếm tin trên internet");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 740));
        setLocationRelativeTo(null);

        setContentPane(buildUI());
        wireEvents();

        if (!settings.isReady()) SettingsDialog.open(this, settings, false);
        else setStatus("Sẵn sàng. Nhập từ khóa và bấm Tìm kiếm.");
    }

    private JComponent buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        root.add(Pretty.gradientHeader("SpiderWeb Search",
                "Google Programmable Search Engine (CSE) + Java Swing", btnSettings), BorderLayout.NORTH);

        JSplitPane splitA = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildDomainsCard(), buildKeywordsCard());
        splitA.setResizeWeight(0.26);
        splitA.setBorder(null);

        JSplitPane splitB = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                splitA, buildResultsCard());
        splitB.setResizeWeight(0.55);
        splitB.setBorder(null);

        root.add(splitB, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        return root;
    }

    private JComponent buildDomainsCard() {
        JPanel card = Pretty.card();
        card.setLayout(new BorderLayout(10, 10));
        card.add(Pretty.sectionTitle("Danh sách web (lọc)"), BorderLayout.NORTH);

        domainList.setCellRenderer(new Renderers.DomainRenderer());
        domainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        domainList.setBorder(new EmptyBorder(6, 6, 6, 6));

        domainModel.addElement(new DomainItem("Tất cả", 0));
        domainList.setSelectedIndex(0);

        JScrollPane sp = new JScrollPane(domainList);
        sp.setBorder(BorderFactory.createEmptyBorder());
        card.add(sp, BorderLayout.CENTER);

        card.add(new JLabel("<html><span style='color:#666'>Chọn domain để lọc kết quả</span></html>"),
                BorderLayout.SOUTH);

        return card;
    }

    private JComponent buildKeywordsCard() {
        JPanel card = Pretty.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.add(Pretty.sectionTitle("Từ khóa"));
        card.add(Box.createVerticalStrut(10));

        Pretty.styleTextField(tfK1, "Từ khóa 1 (bắt buộc)");
        Pretty.styleTextField(tfK2, "Từ khóa 2 (tuỳ chọn)");
        Pretty.styleTextField(tfK3, "Từ khóa 3 (tuỳ chọn)");

        card.add(tfK1); card.add(Box.createVerticalStrut(10));
        card.add(tfK2); card.add(Box.createVerticalStrut(10));
        card.add(tfK3); card.add(Box.createVerticalStrut(14));

        JPanel btns = new JPanel(new GridLayout(2, 1, 0, 10));
        btns.setOpaque(false);
        btns.add(btnSearch);
        btns.add(btnPrice);

        card.add(btns);
        card.add(Box.createVerticalStrut(12));
        card.add(new JLabel("<html><span style='color:#666'>Mẹo: Ctrl+Enter để tìm nhanh</span></html>"));
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JComponent buildResultsCard() {
        JPanel card = Pretty.card();
        card.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);

        top.add(Pretty.sectionTitle("Kết quả"), BorderLayout.WEST);
        lbMeta.setForeground(new Color(90, 90, 90));
        top.add(lbMeta, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        btnPrev.setEnabled(false); btnNext.setEnabled(false);
        btnOpen.setEnabled(false); btnCopy.setEnabled(false);

        actions.add(btnPrev);
        actions.add(btnNext);
        actions.add(btnCopy);
        actions.add(btnOpen);

        top.add(actions, BorderLayout.EAST);

        resultList.setCellRenderer(new Renderers.ResultRenderer(() -> lastKeywords));
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane sp = new JScrollPane(resultList);
        sp.setBorder(BorderFactory.createEmptyBorder());

        card.add(top, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private JComponent buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(6, 2, 0, 2));

        lbStatus.setForeground(new Color(70, 70, 70));
        progress.setVisible(false);

        bar.add(lbStatus, BorderLayout.WEST);
        bar.add(progress, BorderLayout.EAST);

        return bar;
    }

    private void wireEvents() {
        btnSettings.addActionListener(e -> SettingsDialog.open(this, settings, true));
        btnSearch.addActionListener(e -> startSearch(false, true));
        btnPrice.addActionListener(e -> startSearch(true, true));

        // Ctrl+Enter = Search
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "SEARCH");
        getRootPane().getActionMap().put("SEARCH", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { startSearch(false, true); }
        });

        domainList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                DomainItem di = domainList.getSelectedValue();
                domainFilter = (di == null) ? "Tất cả" : di.domain();
                applyFilter();
            }
        });

        resultList.addListSelectionListener(e -> {
            ResultItem it = resultList.getSelectedValue();
            boolean ok = it != null && !it.link().isBlank();
            btnOpen.setEnabled(ok);
            btnCopy.setEnabled(ok);
        });

        resultList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int idx = resultList.locationToIndex(e.getPoint());
                    if (idx >= 0) resultList.setSelectedIndex(idx);
                    showPopup(e.getComponent(), e.getX(), e.getY());
                } else if (e.getClickCount() == 2) {
                    ResultItem it = resultList.getSelectedValue();
                    if (it != null) Utils.openBrowser(it.link());
                }
            }
        });

        btnOpen.addActionListener(e -> {
            ResultItem it = resultList.getSelectedValue();
            if (it != null) Utils.openBrowser(it.link());
        });

        btnCopy.addActionListener(e -> {
            ResultItem it = resultList.getSelectedValue();
            if (it != null && !it.link().isBlank()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(it.link()), null);
                toast("Đã copy link.");
            }
        });

        btnPrev.addActionListener(e -> {
            if (startIndex - pageSize >= 1) {
                startIndex -= pageSize;
                startSearch(false, false);
            }
        });

        btnNext.addActionListener(e -> {
            if (startIndex + pageSize <= Math.max(1, totalResults)) {
                startIndex += pageSize;
                startSearch(false, false);
            }
        });
    }

    private void showPopup(Component c, int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem open = new JMenuItem("Mở link");
        JMenuItem copy = new JMenuItem("Copy link");

        ResultItem it = resultList.getSelectedValue();
        boolean ok = it != null && !it.link().isBlank();
        open.setEnabled(ok);
        copy.setEnabled(ok);

        open.addActionListener(e -> Utils.openBrowser(it.link()));
        copy.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(it.link()), null);
            toast("Đã copy link.");
        });

        menu.add(open);
        menu.add(copy);
        menu.show(c, x, y);
    }

    private void startSearch(boolean priceMode, boolean resetPage) {
        if (!settings.isReady()) {
            SettingsDialog.open(this, settings, false);
            return;
        }

        List<String> keywords = Utils.collectKeywords(tfK1.getText(), tfK2.getText(), tfK3.getText());
        if (keywords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập ít nhất Từ khóa 1.", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        lastKeywords = keywords;
        String query = String.join(" ", keywords) + (priceMode ? " giá" : "");
        lastQuery = query;
        if (resetPage) startIndex = 1;

        setBusy(true, "Đang tìm: " + query);

        SwingWorker<SearchResponse, Void> worker = new SwingWorker<>() {
            @Override protected SearchResponse doInBackground() throws Exception {
                return client.search(query, pageSize, startIndex);
            }
            @Override protected void done() {
                try {
                    SearchResponse r = get();
                    totalResults = r.totalResults();
                    allResults = r.items();

                    rebuildDomains(allResults);
                    domainFilter = "Tất cả";
                    domainList.setSelectedIndex(0);

                    applyFilter();
                    updateMeta();
                    updatePaging();
                    setBusy(false, "Xong.");
                } catch (Exception ex) {
                    setBusy(false, "Có lỗi");
                    JOptionPane.showMessageDialog(SpiderWebApp.this,
                            "Lỗi: " + ex.getMessage(),
                            "API Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void rebuildDomains(List<ResultItem> items) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ResultItem it : items) counts.put(it.displayLink(), counts.getOrDefault(it.displayLink(), 0) + 1);

        domainModel.clear();
        domainModel.addElement(new DomainItem("Tất cả", items.size()));
        for (var e : counts.entrySet()) domainModel.addElement(new DomainItem(e.getKey(), e.getValue()));
    }

    private void applyFilter() {
        resultModel.clear();

        List<ResultItem> view = new ArrayList<>();
        for (ResultItem it : allResults) {
            if ("Tất cả".equals(domainFilter) || it.displayLink().equalsIgnoreCase(domainFilter)) view.add(it);
        }

        if (view.isEmpty()) {
            resultModel.addElement(new ResultItem("Không có kết quả theo bộ lọc", "", "Hãy chọn “Tất cả” hoặc tìm lại.", domainFilter));
        } else {
            for (ResultItem it : view) resultModel.addElement(it);
        }
    }

    private void updateMeta() {
        long page = Utils.pageOf(startIndex, pageSize);
        long pages = Utils.pageCount(totalResults, pageSize);
        lbMeta.setText("  Page " + page + "/" + pages + " • Total: " + totalResults + " • start=" + startIndex + " • q=\"" + lastQuery + "\"");
    }

    private void updatePaging() {
        long pages = Utils.pageCount(totalResults, pageSize);
        btnPrev.setEnabled(startIndex - pageSize >= 1 && pages > 1);
        btnNext.setEnabled(startIndex + pageSize <= Math.max(1, totalResults) && pages > 1);
    }

    private void setBusy(boolean busy, String status) {
        btnSearch.setEnabled(!busy);
        btnPrice.setEnabled(!busy);
        btnSettings.setEnabled(!busy);

        progress.setVisible(busy);
        progress.setIndeterminate(busy);

        setStatus(status);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private void setStatus(String msg) {
        lbStatus.setText(msg);
    }

    private void toast(String msg) {
        setStatus(msg);
        javax.swing.Timer t = new javax.swing.Timer(2200, e -> setStatus("Sẵn sàng"));
        t.setRepeats(false);
        t.start();
    }

    public static void main(String[] args) {
        Pretty.installLookAndFeel();
        SwingUtilities.invokeLater(() -> new SpiderWebApp().setVisible(true));
    }
}
