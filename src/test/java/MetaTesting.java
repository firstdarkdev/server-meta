import dev.firstdark.servermeta.readers.ForgeMetaReader;
import dev.firstdark.servermeta.readers.PaperMetaReader;

public class MetaTesting {

    public static void main(String[] args) {
        ForgeMetaReader metaReader = new ForgeMetaReader();
        metaReader.start();

        /*PaperMetaReader metaReader = new PaperMetaReader();
        metaReader.start();*/
    }

}
