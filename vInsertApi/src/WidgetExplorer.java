import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.insertion.IClient;
import org.vinsert.insertion.IWidget;

import api.ScriptBase;

@ScriptManifest(authors = { "Fortruce" }, name = "WidgetExplorer")
public class WidgetExplorer extends ScriptBase {
	
	private WidgetExplorerGui gui;
	private static boolean guiWait = true;
	public static Rectangle drawWidget;
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
//		gui = new WidgetExplorerGui(this.getContext());
//		gui.setVisible(true);
//		
//		while(guiWait) sleep(500);
		
//		getWidgets(new DefaultMutableTreeNode("widgets"));
		(new GuiThread(getContext())).start();
		
		return true;
	}
	
	public DefaultMutableTreeNode getWidgets(DefaultMutableTreeNode root) {
		ScriptContext ctx = getContext();
		IClient client = ctx.getClient();
		IWidget[][] widgs = client.getWidgets();
		ArrayList<Integer> parents = new ArrayList<Integer>();
		if (widgs == null) {
			log("widgs == null");
			return null;
		}
		for (int i = 0; i < widgs.length; i++) {
			if (widgs[i] != null) {
				for (int j = 0; j < widgs[i].length; j++) {
					if (widgs[i][j] != null) {
						Widget w = new Widget(ctx, widgs[i][j]);
						if (w != null) {
							if (!parents.contains(w.getParentId())){
								parents.add(w.getParentId());
								log(String.format("parent added: %d  - index: %d", w.getParentId(), i));
							}
							//log(String.format("%d - %d", w.getParentId(), w.getId()));
						}
					}
				}
			}
		}
		log("PARENTS");
		for (Integer w : parents) {
			log(printWidget(w));
			DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(w);
			root.add(parentNode);
			for (int j = 0; j < widgs[w].length; j++) {
				if (widgs[w][j] != null) {
					WidgetWrapper child = new WidgetWrapper(new Widget(ctx, widgs[w][j]));
					if (child != null)
						parentNode.add(new DefaultMutableTreeNode(child));
				}
			}
		}
		log("END");
		return root;
	}
	
	public String printWidget(Integer w) {
		return String.format("%d", w);
	}

	public String widgetToString(Widget wid){
		StringBuilder widgetInfo = new StringBuilder();
		if(wid != null){
			widgetInfo.append("Height: ");
			widgetInfo.append(wid.getHeight());
			widgetInfo.append("\nWidth: ");
			widgetInfo.append(wid.getWidth());
			widgetInfo.append("\n");
			if (wid.getLocation() != null) {
				widgetInfo.append("Location: ");
				widgetInfo.append(String.format("[%d,  %d]", wid.getLocation().x, wid.getLocation().y));
				widgetInfo.append("\n");
			}
			if (wid.getActions() != null) {
				widgetInfo.append("Actions: ");
				for(int x = 0; x <  wid.getActions().length; x++){
					widgetInfo.append(wid.getActions()[x]);
					if(x != wid.getActions().length - 1)
						widgetInfo.append(",");
					else
						widgetInfo.append("\n");
				}
			}
			widgetInfo.append("Id: ");
			widgetInfo.append(wid.getId());
			widgetInfo.append("\n");
			widgetInfo.append("Tooltip: ");
			widgetInfo.append(wid.getTooltip());
			widgetInfo.append("\n");
			widgetInfo.append("SpellName: ");
			widgetInfo.append(wid.getSpellName());
			widgetInfo.append("\n");
			return widgetInfo.toString();
		}
		return "null";
	}
	
	public static void paintWidget(Widget w) {
		if (w != null) {
			int x, y;
			Widget parent = w.getParent();
			if (parent != null) {
				x = parent.getBounds().x + w.getBounds().x;
				y = parent.getBounds().y + w.getBounds().y;
			} else {
				x = w.getBounds().x;
				y = w.getBounds().y;
			}
			WidgetExplorer.drawWidget = new Rectangle(x, y, w.getWidth(), w.getHeight());
		}
	}
	

	@Override
	public void render(Graphics2D g) {
        g.setColor(new Color(63, 63, 43, 200));
        
        int[] point = {385, 2};
        int height = g.getFontMetrics().getHeight();
        
        //text
        g.setColor(Color.WHITE);
        g.drawString("Fortruce - Widget", point[0] + 5, point[1] += height);
        g.drawLine(389, 21, 499, 21);
        
        
		Rectangle w = WidgetExplorer.drawWidget;
		if (w != null) {
			g.drawRect(w.x, w.y, w.width, w.height);
		}
	}
	
	class GuiThread extends Thread {
		private ScriptContext ctx;
		
		public GuiThread(ScriptContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public void run() {
			WidgetExplorerGui gui = new WidgetExplorerGui(this.ctx);
			gui.setVisible(true);
		}
	}

	/**
	 * @author Joseph Rollins
	 */
	@SuppressWarnings("serial")
	class WidgetExplorerGui extends JFrame {
		
		private ScriptContext ctx;
		private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Widgets");
		
		public WidgetExplorerGui(ScriptContext ctx) {
			this.ctx = ctx;
			initComponents();
		}

		private void bUpdateActionPerformed(ActionEvent e) {
			// TODO add your code here
		}

		private void thisWindowClosing(WindowEvent e) {
			WidgetExplorer.guiWait = false;
		}
		
		private void widgetTreeValueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) widgetTree.getLastSelectedPathComponent();
			if (selectedNode.isLeaf()) {
				Widget wid = ((WidgetWrapper)selectedNode.getUserObject()).widget;
				if (wid != null) {
					widgetInfo.setText(widgetToString(wid));
					WidgetExplorer.paintWidget(wid);
				}
			}
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			// Generated using JFormDesigner Evaluation license - Joseph Rollins
			bUpdate = new JButton();
			tfSearch = new JTextField();
			scrollPane1 = new JScrollPane();
			
			root = getWidgets(root);
			widgetTree = new JTree(root);
			
			scrollPane2 = new JScrollPane();
			widgetInfo = new JTextPane();
			widgetInfo.setEditable(false);
			
			widgetTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			widgetTree.setRootVisible(false);

			//======== this ========
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					thisWindowClosing(e);
				}
			});
			Container contentPane = getContentPane();

			//---- bUpdate ----
			bUpdate.setText("Update");
			bUpdate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					bUpdateActionPerformed(e);
				}
			});

			//======== scrollPane1 ========
			{

				//---- widgetTree ----
				widgetTree.addTreeSelectionListener(new TreeSelectionListener() {
					@Override
					public void valueChanged(TreeSelectionEvent e) {
						widgetTreeValueChanged(e);
					}
				});
				scrollPane1.setViewportView(widgetTree);
			}

			//======== scrollPane2 ========
			{
				scrollPane2.setViewportView(widgetInfo);
			}

			GroupLayout contentPaneLayout = new GroupLayout(contentPane);
			contentPane.setLayout(contentPaneLayout);
			contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(contentPaneLayout.createParallelGroup()
							.addGroup(contentPaneLayout.createSequentialGroup()
								.addComponent(tfSearch, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
								.addGap(18, 18, 18)
								.addComponent(bUpdate, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
							.addGroup(contentPaneLayout.createSequentialGroup()
								.addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)))
						.addContainerGap())
			);
			contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(bUpdate)
							.addComponent(tfSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(contentPaneLayout.createParallelGroup()
							.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
							.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
						.addContainerGap())
			);
			pack();
			setLocationRelativeTo(getOwner());
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		// Generated using JFormDesigner Evaluation license - Joseph Rollins
		private JButton bUpdate;
		private JTextField tfSearch;
		private JScrollPane scrollPane1;
		public JTree widgetTree;
		private JScrollPane scrollPane2;
		private JTextPane widgetInfo;
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}
	
	class WidgetWrapper{

		private Widget widget;
		
		public WidgetWrapper(Widget widget) {
			this.widget = widget;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("%d", this.getId());
		}

		private int getParentId() {
			return this.widget.getParentId();
		}

		private int getId() {
			return widget.getId();
		}
	}

}