package com.els.facturacion.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AutoCompleteComboBox extends JComboBox<String> {

    private List<String> items;
    private boolean setting = false;
    private boolean navigating = false;
    private boolean programmaticPopup = false;
    private Timer debounceTimer;
    private Runnable filterCallback;
    private KeyAdapter arrowKeyListener;
    private DocumentListener filterListener;
    private java.awt.event.ActionListener enterSelectListener;

    public AutoCompleteComboBox() {
        super();
        this.items = new ArrayList<>();
        setEditable(true);
        debounceTimer = new Timer(80, e -> doFilter());
        debounceTimer.setRepeats(false);
        attachListeners();
    }

    public void refreshListeners() {
        attachListeners();
    }

    private void attachListeners() {
        if (getEditor() == null) return;
        JTextField editor = (JTextField) getEditor().getEditorComponent();
        if (editor == null) return;

        // Remove old listeners first to prevent duplicates
        if (arrowKeyListener != null) editor.removeKeyListener(arrowKeyListener);
        if (filterListener != null) editor.getDocument().removeDocumentListener(filterListener);
        if (enterSelectListener != null) editor.removeActionListener(enterSelectListener);

        arrowKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (isPopupVisible()) {
                        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
                        if (model.getSize() > 0) {
                            int current = getSelectedIndex();
                            int next = (e.getKeyCode() == KeyEvent.VK_DOWN)
                                ? Math.min(current + 1, model.getSize() - 1)
                                : Math.max(current - 1, 0);
                            if (next != current) {
                                navigating = true;
                                setSelectedIndex(next);
                                navigating = false;
                            }
                        }
                        e.consume();
                    }
                }
            }
        };
        editor.addKeyListener(arrowKeyListener);

        filterListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!setting) debounceTimer.restart();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!setting) debounceTimer.restart();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };
        editor.getDocument().addDocumentListener(filterListener);

        enterSelectListener = e -> SwingUtilities.invokeLater(() -> {
            debounceTimer.stop();
            if (getSelectedIndex() < 0) {
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
                if (model.getSize() > 0) {
                    setSelectedIndex(0);
                }
            }
        });
        editor.addActionListener(enterSelectListener);
    }

    private void doFilter() {
        if (setting || items == null) return;

        JTextField editor = (JTextField) getEditor().getEditorComponent();
        String text = editor.getText();
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();

        setting = true;
        model.removeAllElements();

        String textLower = text.isEmpty() ? "" : text.toLowerCase();
        for (String item : items) {
            if (item == null) continue;
            if (textLower.isEmpty() || item.toLowerCase().contains(textLower)) {
                model.addElement(item);
            }
        }

        if (model.getSize() > 0 && !textLower.isEmpty()) {
            super.setSelectedIndex(0);
            setting = true;
            editor.setText(text);
            programmaticPopup = true;
            if (isShowing()) showPopup();
        } else {
            super.setSelectedItem(null);
            setting = true;
            editor.setText(text);
            if (isShowing()) hidePopup();
        }
        setting = false;
        if (filterCallback != null) {
            filterCallback.run();
        }
    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && !programmaticPopup && items != null) {
            setting = true;
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
            model.removeAllElements();
            for (String item : items) {
                model.addElement(item);
            }
            setting = false;
        }
        if (!visible) {
            programmaticPopup = false;
        }
        super.setPopupVisible(visible);
    }

    public void setOnFilter(Runnable callback) {
        this.filterCallback = callback;
    }

    public void setData(List<String> newItems) {
        setting = true;
        this.items = new ArrayList<>(newItems);
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
        model.removeAllElements();
        for (String item : items) {
            model.addElement(item);
        }
        setting = false;
    }

    public String getEditorText() {
        JTextField editor = (JTextField) getEditor().getEditorComponent();
        return editor.getText();
    }

    public void setEditorText(String text) {
        setting = true;
        JTextField editor = (JTextField) getEditor().getEditorComponent();
        editor.setText(text);
        setting = false;
    }

    @Override
    public void fireActionEvent() {
        if (!setting && !navigating) {
            super.fireActionEvent();
        }
    }
}
