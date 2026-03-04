package org.l2o1.myink.presenter;

import org.l2o1.myink.model.FileManager;
import org.l2o1.myink.model.NodePool;
import org.l2o1.myink.model.util.BookObject;
import org.l2o1.myink.model.util.PageLink;
import org.l2o1.myink.model.util.PageNode;
import org.l2o1.myink.view.EditingView;

import java.io.File;

public class EditingPresenter {

    private final EditingView view;
    public String title;
    public NodePool pool;
    public PageNode currentNode;

    public EditingPresenter(EditingView view, String title) {
        this.view = view;
        this.title = title;
        this.pool = this.getNodePool();
        this.currentNode = this.pool.getBookGraph();
    }

    public String getCover() {
        return FileManager.getCoverPath(this.title);
    }

    public boolean copyFile(File source, File dest) {
        return FileManager.copyFile(source, dest);
    }

    public String getAvailableFilename(String title) {
        return FileManager.getAvailableFilenameByTitle(title);
    }

    public boolean remaneDir(String oldTitle, String newTitle) {
        return FileManager.renameDirectory(FileManager.titleToPath(oldTitle), FileManager.titleToPath(newTitle));
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
