package com.els.facturacion.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;

public class AutoCompleteComboBox extends JComboBox<String> {

    private List<String> items;
    private boolean setting = false;

    public AutoCompleteComboBox() {
        super();
        this.items = new ArrayList<>();
        setEditable(true);
        initListener();
    }

    private void initListener() {
        JTextField editor = (JTextField) getEditor().getEditorComponent();
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!setting) SwingUtilities.invokeLater(() -> filter());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!setting) SwingUtilities.invokeLater(() -> filter());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void filter() {
        if (setting || items == null) return;

        JTextField editor = (JTextField) getEditor().getEditorComponent();
        String text = editor.getText();
        String textLower = text.toLowerCase();
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();

        setting = true;
        model.removeAllElements();

        for (String item : items) {
            if (textLower.isEmpty() || item.toLowerCase().contains(textLower)) {
                model.addElement(item);
            }
        }

        super.setSelectedItem(null);
        editor.setText(text);
        setting = false;

        if (!textLower.isEmpty() && model.getSize() > 0) {
            showPopup();
        } else {
            hidePopup();
        }
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
    public void setSelectedItem(Object item) {
        setting = true;
        super.setSelectedItem(item);
        setting = false;
    }

    @Override
    public void setSelectedIndex(int index) {
        setting = true;
        super.setSelectedIndex(index);
        setting = false;
    }
}
