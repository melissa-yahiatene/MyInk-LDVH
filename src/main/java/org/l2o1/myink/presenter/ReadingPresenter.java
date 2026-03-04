package org.l2o1.myink.presenter;

import org.l2o1.myink.model.FileManager;
import org.l2o1.myink.model.NodePool;
import org.l2o1.myink.model.ObjectInventory;
import org.l2o1.myink.model.util.PageNode;
import org.l2o1.myink.view.ReadingView;

public class ReadingPresenter {

    private final ReadingView view;
    public String title;
    public NodePool pool;
    public ObjectInventory inventory;
    public PageNode currentNode;

    public ReadingPresenter(ReadingView view, String title) {
        this.view = view;
        this.title = title;
        this.pool = this.getNodePool();
        this.inventory = new ObjectInventory();
        this.currentNode = this.pool.getBookGraph();
    }

    public String getCover() {
        return FileManager.getCoverPath(this.title);
    }

    public void saveBook() {
        FileManager.saveBook(this.title, this.pool);
    }

    public NodePool getNodePool() {
        return FileManager.getNodePool(this.title);
    }

    public String getBookObjectPath(String title, String objectName) {
        return FileManager.getBookObjectPath(title, objectName);
    }
}
