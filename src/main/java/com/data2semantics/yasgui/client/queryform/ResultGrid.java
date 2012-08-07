package com.data2semantics.yasgui.client.queryform;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.RdfNodeContainer;
import com.data2semantics.yasgui.shared.ResultSetContainer;
import com.data2semantics.yasgui.shared.SolutionContainer;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ResultGrid extends ListGrid {
	private View view;

	public ResultGrid(View view) {
		setWidth100();
		setHeight(350);
		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);
		setShowRowNumbers(true);
//		setFixedRecordHeights(false);
		setAutoFitData(Autofit.VERTICAL);
		setCanResizeFields(true);
		setEmptyMessage("Executing query");
		this.view = view;
	}

	public ResultGrid(View view, ResultSetContainer resultSet) {
		this(view);
		drawQueryResults(resultSet);
	}

	public void drawQueryResults(ResultSetContainer resultSet) {
		List<ListGridField> listGridFields = getVarsAsListGridFields(resultSet.getResultVars());
		setFields(listGridFields.toArray(new ListGridField[listGridFields.size()]));

		List<ListGridRecord> listGridRecords = getSolutionsAsGridRecords(resultSet.getQuerySolutions());
		setData(listGridRecords.toArray(new ListGridRecord[listGridRecords.size()]));
	}

	protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
		// fieldname is the identifier of the column, in our case the same as
		// the column header
		String fieldName = this.getFieldName(colNum);
		getView().getLogger().severe(fieldName);
		if (fieldName.startsWith("yasgui__")) {
			String varName = fieldName.substring("yasgui__".length());
			if (record.getAttributeAsBoolean(varName + "__isUri__")) {
				getView().getLogger().severe(varName + ": " + Integer.toString(colNum));
				String value = record.getAttributeAsString(varName);
//				DynamicForm form = new DynamicForm();
//				LinkItem linkItem = new LinkItem("link");
//				linkItem.setShowTitle(false);
//				linkItem.setLinkTitle(value);
//				linkItem.setTarget(value);
//				form.setFields(linkItem);
//				return form;
//				Label label = new Label("firstIf" + value);
//				label.setHeight(5);
//				return label;
				HTMLPane html = new HTMLPane();
				html.setContents("<a href=\"" + value + "\" target=\"_blank\">" + value + "</a>");
				html.setHeight100();
				html.setWidth100();
				return html;
			} else {
				Label label = new Label(record.getAttributeAsString(varName));
				label.setHeight(5);
				return label;
			}
		}
		return null;
	}

	private List<ListGridRecord> getSolutionsAsGridRecords(List<SolutionContainer> querySolutions) {
		List<ListGridRecord> listGridRecords = new ArrayList<ListGridRecord>();
		for (SolutionContainer solution : querySolutions) {
			ListGridRecord listGridRecord = new ListGridRecord();
			List<RdfNodeContainer> nodes = solution.getRdfNodes();
			for (RdfNodeContainer node : nodes) {

				listGridRecord.setAttribute(node.getVarName(), getValueForField(node));
				listGridRecord.setAttribute(node.getVarName() + "__isUri__", node.isUri());
			}
			listGridRecords.add(listGridRecord);
		}
		return listGridRecords;
	}

	private String getValueForField(RdfNodeContainer node) {
		String value = "";
		if (node.isAnon()) {
			value = node.getValue();
		} else if (node.isLiteral()) {
			value = node.getValue();
		} else if (node.isUri()) {
			value = node.getValue();

		}
		return value;
	}

	private List<ListGridField> getVarsAsListGridFields(List<String> resultVars) {
		List<ListGridField> listGridFields = new ArrayList<ListGridField>();
		for (String resultVar : resultVars) {
			ListGridField field = new ListGridField("yasgui__" + resultVar, resultVar);
			listGridFields.add(field);
		}
		return listGridFields;
	}

	private View getView() {
		return this.view;
	}
}
