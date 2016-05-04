package game.editor;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
	
	JSpinner spinner;

	public SpinnerEditor(JSpinner spinner) {
		this.spinner = spinner;
	}

	public boolean isCellEditable(EventObject evt) {
		if (evt instanceof MouseEvent) {
			return ((MouseEvent) evt).getClickCount() >= 2;
		}
		return true;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int column) {
		spinner.setValue(value);
		return spinner;
	}

	public Object getCellEditorValue() {
		return spinner.getValue();
	}
}