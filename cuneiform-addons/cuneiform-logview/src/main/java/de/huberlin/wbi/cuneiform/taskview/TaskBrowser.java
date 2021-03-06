package de.huberlin.wbi.cuneiform.taskview;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.huberlin.wbi.cuneiform.core.semanticmodel.JsonReportEntry;
import de.huberlin.wbi.cuneiform.logview.common.Visualizable;

public class TaskBrowser extends Visualizable {

	private static final long serialVersionUID = 2782394011894606586L;
	
	private final DefaultMutableTreeNode top;
	private DefaultMutableTreeNode unnamed;
	private final Map<Long,InvocationItem> invocMap;
	private final Map<String,DefaultMutableTreeNode> taskMap;
	private final JTree tree;
	private final DefaultTreeModel treeModel;

	public TaskBrowser( TaskView taskView ) {
		
		JScrollPane scrollPane;
		
		if( taskView == null )
			throw new NullPointerException( "Task view must not be null." );
		
		invocMap = new HashMap<>();
		taskMap = new HashMap<>();
		
		setLayout( new BorderLayout() );
		
		top = new DefaultMutableTreeNode( "Cuneiform tasks" );
		
		treeModel = new DefaultTreeModel( top );
		tree = new JTree( treeModel );
		tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		
		scrollPane = new JScrollPane( tree );
		scrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

		add( scrollPane, BorderLayout.CENTER );
		
		tree.addTreeSelectionListener( taskView );
		
	}
	
	@Override
	public void register( JsonReportEntry entry ) {
		
		long invocId;
		InvocationItem invocItem;
		String taskName;
		DefaultMutableTreeNode taskItem;
		
		if( !entry.hasInvocId() )
			return;
		
		tree.expandPath( new TreePath( top.getPath() ) );

		
		invocId = entry.getInvocId();
		
		invocItem = invocMap.get( invocId );
		if( invocItem == null ) {
			
			
			taskName = null;
			if( entry.hasTaskname() ) {
				
				taskName = entry.getTaskName();
				taskItem = taskMap.get( taskName );
				if( taskItem == null ) {
					taskItem = new DefaultMutableTreeNode( taskName );
					taskMap.put( taskName, taskItem );
					treeModel.insertNodeInto( taskItem, top, top.getChildCount() );
					top.add( taskItem );
				}
			}
			else {
				
				if( unnamed == null ) {
					unnamed = new DefaultMutableTreeNode( "[lambda]" );
					treeModel.insertNodeInto( unnamed, top, 0 );
				}
				taskItem = unnamed;
			}
			invocItem = new InvocationItem( invocId, taskName );
			invocMap.put( invocId, invocItem );

			treeModel.insertNodeInto( invocItem, taskItem, taskItem.getChildCount() );
		}
		
		if( entry.isKeyInvocStdErr() )
			invocItem.setStdErr( entry.getValueRawString() );
		
		if( entry.isKeyInvocStdOut() )
			invocItem.setStdOut( entry.getValueRawString() );
		
	}

	@Override
	public void clear() {
		
		int i;
		
		for( i = treeModel.getChildCount( top )-1; i >= 0; i-- )
			treeModel.removeNodeFromParent( ( DefaultMutableTreeNode )top.getChildAt( i ) );
	}

	@Override
	public void updateView() {
		// nothing to do
	}
}
