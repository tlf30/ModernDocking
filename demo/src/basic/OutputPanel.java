package basic;

import ModernDocking.DockableStyle;
import ModernDocking.persist.AppState;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.*;

public class OutputPanel extends ToolPanel {
    private JTable table = new JTable(new DefaultTableModel(new String[] { "one", "two"}, 0));

    private Map<String, String> properties = new HashMap<>();

    public OutputPanel(String title, String persistentID, DockableStyle style, Icon icon) {
        super(title, persistentID, style, icon);

        add(new JScrollPane(table));

        updateColumnsProp();
        updateColumnSizesProp();

        table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            @Override
            public void columnAdded(TableColumnModelEvent e) {
            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
                updateColumnsProp();

                AppState.persist();
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
                updateColumnSizesProp();

                AppState.persist();
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
            }
        });
    }

    private void updateColumnsProp() {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        String prop = "";

        while (columns.hasMoreElements()) {
            prop += columns.nextElement().getHeaderValue().toString();
            prop += ",";
        }

        properties.put("columns", prop);
    }

    private void updateColumnSizesProp() {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        String prop = "";

        while (columns.hasMoreElements()) {
            prop += columns.nextElement().getWidth();
            prop += ",";
        }

        properties.put("column-sizes", prop);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        if (properties.get("columns") != null && properties.get("column-sizes") != null) {
            String[] columns = properties.get("columns").split(",");
            String[] columnSizes = properties.get("column-sizes").split(",");


            List<TableColumn> tableColumns = Collections.list(table.getColumnModel().getColumns());

            for (int i = 0; i < columns.length; i++) {
                int location = table.getColumnModel().getColumnIndex(columns[i]);

                table.getColumnModel().moveColumn(location, i);
                final int index = i;
                SwingUtilities.invokeLater(() -> {
                    table.getColumnModel().getColumn(index).setPreferredWidth(Integer.parseInt(columnSizes[index]));
                });
            }
        }
    }
}
