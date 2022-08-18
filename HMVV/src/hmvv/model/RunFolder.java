package hmvv.model;

public class RunFolder {
    public final String runFolderName;

    public RunFolder(String runFolderName){
        this.runFolderName = runFolderName;
    }

    public String toString(){
        return runFolderName;
    }

    public boolean equals(Object o){
        if (o instanceof RunFolder){
            return ((RunFolder)o).runFolderName.equals(runFolderName);
        }
        return false;
    }
}
