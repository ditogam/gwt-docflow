package com.docflow.client.components;

import java.util.HashMap;
import java.util.Set;

import com.docflow.client.DocFlow;
import com.docflow.client.components.docflow.DocTypeTreeGrid;
import com.docflow.client.components.docflow.DocumentDetailTabPane;
import com.docflow.client.components.docflow.DocumentSearchForm;
import com.docflow.client.components.docflow.PDocStatuses;
import com.docflow.client.components.hr.HRStructureTree;
import com.docflow.shared.SCSystem;
import com.docflow.shared.SystemNames;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class NavigationSectionStack extends SectionStack {

	private static final String SYSTEM_NAME = "___SYSTEM_NAME";

	private HashMap<Integer, DocTypeTreeGrid> mpSystemGrids = new HashMap<Integer, DocTypeTreeGrid>();

	public NavigationSectionStack(final int system) {
		super();
		setVisibilityMode(VisibilityMode.MUTEX);

		if (system < SCSystem.S_HR) {

			Set<Integer> systems = DocFlow.user_obj.getStatusTree().keySet();

			for (Integer system_id : systems) {
				SectionStackSection documenstSection = new SectionStackSection(
						SystemNames.getSystemName(system_id));
				documenstSection.setExpanded(system_id == DocFlow.user_obj
						.getInitial_system());
				documenstSection.setResizeable(false);
				DocTypeTreeGrid grid = new DocTypeTreeGrid(system_id);
				mpSystemGrids.put(system_id, grid);
				documenstSection.setItems(grid);
				documenstSection.setAttribute(SYSTEM_NAME, system_id);
				addSection(documenstSection);

			}
			addSectionHeaderClickHandler(new SectionHeaderClickHandler() {

				@Override
				public void onSectionHeaderClick(SectionHeaderClickEvent event) {
					DocumentSearchForm.instance.saveStatus();
					mpSystemGrids.get(DocFlow.system_id).saveStatus();
					DocFlow.system_id = event.getSection().getAttributeAsInt(
							SYSTEM_NAME);

					DocumentDetailTabPane.documentDetails.setStatuses();
					DocumentSearchForm.instance.setStatuses();
					mpSystemGrids.get(DocFlow.system_id).setCurrentDocType();
					PDocStatuses.instance.recreateStatuses();
				}
			});

		}
		if (system == DocFlow.S_HR) {
			SectionStackSection hresourcesSection = new SectionStackSection(
					"Human Resource");
			hresourcesSection.setExpanded(true);
			hresourcesSection.setResizeable(false);
			final HRStructureTree hr = new HRStructureTree();
			ToolStrip ts = new ToolStrip();
			final ToolStripButton tsbAddStructure = new ToolStripButton("ST");
			ts.addButton(tsbAddStructure);
			final ToolStripButton tsbPerson = new ToolStripButton("PR");
			ts.addButton(tsbPerson);
			ClickHandler ch = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					int type = -1;
					if (event.getSource().equals(tsbAddStructure))
						type = 1;
					if (event.getSource().equals(tsbPerson))
						type = 2;
					if (type < 1)
						return;
					hr.addItem(true, type);

				}
			};

			tsbAddStructure.addClickHandler(ch);
			tsbPerson.addClickHandler(ch);
			hresourcesSection.setItems(ts, hr);

			addSection(hresourcesSection);
		}
	}
}
