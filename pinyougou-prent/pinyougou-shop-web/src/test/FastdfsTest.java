import com.pinyougou.common.utils.FastDFSClient;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

/**
 * @ClassName:FastdfsTest
 * @Author：Mr.lee
 * @DATE：2019/06/27
 * @TIME： 21:40
 * @Description: TODO
 */
public class FastdfsTest {

    //上传图片
    @Test
    public void uploadFastdfs() throws Exception{

        //1.创建一个配置文件：配置服务器的IP地址和端口

        //2.加载配置文件,要将图片上传到这里的路径的配置文件
        ClientGlobal.init("D:\\IdeaProjects\\SVN\\60pinyougou\\pinyougou-prent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");

        //3.创建trackerClient对象
        TrackerClient trackerClient  = new TrackerClient();

        //4.获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageClient，设置null
        StorageClient storageClient = new StorageClient(trackerServer,null);

        //6.创建storageClient  使用该client的API上传文件即可，切记传自己本地路径的图片
        //参数1表示文件的路径  参数2表示文件的扩展名   参数3表示文件元数据
        String[] jpgs = storageClient.upload_file("D:\\index\\1.jpg", "jpg", null);
        //打印
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }

    //文件上传的工具类，来上传图片
    @Test
    public void uploadFastdfsclient() throws Exception{

        //图片在本地所在的路径
        FastDFSClient fastDFSClient = new FastDFSClient("D:\\IdeaProjects\\SVN\\60pinyougou\\pinyougou-prent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        //图片要上传到的地方
        String jpg = fastDFSClient.uploadFile("D:\\index\\1.jpg", "jpg");
        //打印上传的图片路径
        System.out.println(jpg);
    }

}
