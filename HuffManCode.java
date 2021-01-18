import java.io.*;
import java.util.*;

public class HuffManCode {
    public static void main(String[] args) {

        //测试压缩文件
//        String srcFile = "G:\\333.bmp";
//        String dstFile = "G:\\333.zip";
//
//        zipFile(srcFile,dstFile);
//        System.out.println("压缩成功");

        //测试解压文件
        String unZipSrcFile = "G:\\333.zip";
        String unZipDstFile = "G:\\解压333.bmp";

        unZipFile(unZipSrcFile,unZipDstFile);
        System.out.println("解压成功");


//        String content = "i like like like java do you like a java";
//        byte[] contentBytes = content.getBytes();
//
//        byte[] huffmanCodeBytes = huffmanZip(contentBytes);
//        System.out.println("压缩后的结果" + Arrays.toString(huffmanCodeBytes));
//
//        byte[] sourceByte = decode(huffmanCodes,huffmanCodeBytes);
//        System.out.println("解码后的字符串" + new String(sourceByte));

//        List<Node> nodes = getNodes(contentBytes);
//        System.out.println(nodes);
//
//        //测试创建的赫夫曼树
//        System.out.println("赫夫曼树");
//        Node huffmanTreeRoot = ceateHuffmanTree(nodes);
//        System.out.println("前序遍历");
//        huffmanTreeRoot.preOder();
//
//        //测试是否生成赫夫曼编码
//        getCodes(huffmanTreeRoot);
//        System.out.println("生成的赫夫曼编码表" + huffmanCodes);
//
//        //测试赫夫曼编码压缩后生成的byte数组
//        byte[] huffmanCodeBytes = zip(contentBytes,huffmanCodes);
//        System.out.println("压缩后生成的byte数组 = " + Arrays.toString(huffmanCodeBytes));
    }

    //编写一个方法，完成对压缩文件的解压

    /**
     *
     * @param zipFlie 准备解压的文件
     * @param dstFile 将文件解压到哪个路径
     */
    public static void unZipFile(String zipFlie,String dstFile){

        //定义文件输入流
        InputStream inputStream = null;
        //定义一个对象输入流
        ObjectInputStream objectInputStream = null;
        //定义文件输出流
        OutputStream outputStream = null;

        try {
            //创建文件输入流
            inputStream = new FileInputStream(zipFlie);
            //创建一个和inputStream关联的对象输入流
            objectInputStream = new ObjectInputStream(inputStream);
            //读取byte[]数组 huffmanBytes
            byte[] huffmanBytes = (byte[])objectInputStream.readObject();
            //读取赫夫曼编码表
            Map<Byte,String> huffmanCodes = (Map<Byte, String>)objectInputStream.readObject();

            //解码
            byte[] bytes = decode(huffmanCodes,huffmanBytes);

            //将bytes数组写入到目标文件
            outputStream = new FileOutputStream(dstFile);

            //写数据到dstFile中
            outputStream.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert objectInputStream != null;
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //编写一个方法，将一个文件压缩

    /**
     *
     * @param srcFile 准备压缩文件的路径
     * @param dstFile 压缩好的文件的输出路径
     */
    public static void zipFile(String srcFile,String dstFile){

        //创建输出流
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        //创建输出流
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(srcFile);
            //创建一个和源文件一样大小的byte[]
            byte[] bytes = new byte[fileInputStream.available()];

            //读取文件
            fileInputStream.read(bytes);

            //使用赫夫曼压缩将源文件压缩
            byte[] huffmanBytes = huffmanZip(bytes);

            //使用文件输出流，存放压缩文件
            outputStream = new FileOutputStream(dstFile);

            //创建一个和文件输出流关联的ObjectOutPutStream
            objectOutputStream = new ObjectOutputStream(outputStream);

            //把赫夫曼编码后的字节数组写入压缩文件
            objectOutputStream.writeObject(huffmanBytes);

            //这里以对象流的方式写入赫夫曼编码，为的是以后恢复源文件的时候使用
            //注意一定要把赫夫曼编码写入压缩文件
            objectOutputStream.writeObject(huffmanCodes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fileInputStream != null;
                fileInputStream.close();
                assert outputStream != null;
                outputStream.close();
                assert objectOutputStream != null;
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //完成数据解压
    //思路：
    //1.将huffmanCodeBytes重写成赫夫曼编码对应的二进制字符串
    //2.赫夫曼编码对应的二进制字符串对应赫夫曼编码转成原字符串

    //完成对压缩数据的解码

    /**
     *
     * @param huffmanCodes 赫夫曼编码表map
     * @param huffmanBytes 赫夫曼编码表得到的字节数组
     * @return 原来的字符串对应的数组
     */
    private static byte[] decode(Map<Byte,String> huffmanCodes,byte[] huffmanBytes){
        //1.先得到赫夫曼编码对应的二进制字符串
        StringBuilder stringBuilder = new StringBuilder();
        //将byte数组转成二进制字符串
        for (int i = 0; i < huffmanBytes.length; i++) {
            byte bytes = huffmanBytes[i];
            boolean flag = (i == huffmanBytes.length - 1);
            stringBuilder.append(byteToBitString(!flag,bytes));
        }

        //把字符串按照指定的赫夫曼编码进行解码
        //把赫夫曼编码表进行调换，因为需要反向查询
        Map<String, Byte> map = new HashMap<>();
        for (Map.Entry<Byte,String> entry: huffmanCodes.entrySet()
             ) {
            map.put(entry.getValue(),entry.getKey());
        }

        //创建一个集合存放byte
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < stringBuilder.length();) {
            int count = 1;
            boolean flag = true;
            Byte b = null;

            while (flag){
                String key = stringBuilder.substring(i,i+count);
                b = map.get(key);
                if (b == null){
                    count++;
                }else {
                    flag = false;
                }

            }
            list.add(b);
            i += count;
        }
        //把list中的数据放入到byte[]中并返回
        byte[] b = new byte[list.size()];
        for (int i = 0; i < b.length; i++) {
            b[i] = list.get(i);
        }

        return b;
    }

    /**
     * 将一个byte转成一个二进制字符串
     * @param b 传入的byte
     * @param flag 表示是否需要补高位，如果true表示需要补，false表示不补
     * @return 是b对应的二进制字符串（是按照补码返回）
     */
    private static String byteToBitString(boolean flag, byte b){
        //使用变量保存b
        int temp = b; //将b转成int

        //如果是正数我们还存在补高位问题
        if (flag) {
            temp |= 256;
        }
        String str = Integer.toBinaryString(temp);//返回的是temp对应的二进制补码
        if (flag){
            return str.substring(str.length() - 8);
        }else {
            return str;
        }
    }

    //编写一个方法，将字符串对应的byte[]数组，通过生成的赫夫曼编码表，返回一个赫夫曼编码压缩后的Byte[]数组

    /**
     *
     * @param bytes bytes这是原始的字符串对应的byte数组
     * @param huffmanCodes huffmanCodes生成的赫夫曼编码表
     * @return 返回赫夫曼编码处理过后的byte[]数组
     *
     * HuffmanCodeByte[0] = 10101000(补码) => byte 【推导 10101000 => 1(符号位不变) 0101000(后面数值减1) - 1 =
     * 10100111(反码) => 原码(反码取反） 1(符号位不变) 1011000(其余位置取反) = -88】
     *
     */
    private static byte[] zip(byte[] bytes, Map<Byte,String> huffmanCodes){

        //1.利用hyffmanCodes将bytes转成赫夫曼编码对应的字符串
        StringBuilder stringBuilder = new StringBuilder();
        //遍历byte数组
        for (byte b: bytes
             ) {
            stringBuilder.append(huffmanCodes.get(b));
        }

        //将HuffmanCodes转成byte[]

        //统计返回byte[] huffmanCodeBytes长度
        int len;
        if (stringBuilder.length() % 8 == 0){
            len = stringBuilder.length() / 8;
        }else {
            len = stringBuilder.length() / 8 + 1;
        }

        //创建存储压缩后的byte数组
        byte[] huffmanCodesBytes = new byte[len];
        int index = 0;//记录是第几个byte
        for (int i = 0; i < stringBuilder.length(); i+=8) {
            String strByte;
            if (i + 8 > stringBuilder.length()){
                strByte = stringBuilder.substring(i);
            }else {
                strByte = stringBuilder.substring(i,i+8);
            }
            //将strByte转成一个byte，放入到huffmanCodeBytes
            huffmanCodesBytes[index] = (byte)Integer.parseInt(strByte,2);
            index++;
        }

        return huffmanCodesBytes;

    }

    //编写一个方法将输出赫夫曼编码对应的byte数组的方法封装起来

    /**
     *
     * @param contentBytes 原始字符串对应的字节数组
     * @return 经过赫夫曼编码处理后的字节数组（压缩后的数组）
     */
    private static byte[] huffmanZip(byte[] contentBytes){
        List<Node> nodes = getNodes(contentBytes);
        Node huffmanTreeRoot = ceateHuffmanTree(nodes);
        getCodes(huffmanTreeRoot);
        byte[] huffmanCodeBytes = zip(contentBytes,huffmanCodes);
        return huffmanCodeBytes;
    }

    //生成赫夫曼树对应的赫夫曼编码
    //思路：
    //1.将赫夫曼编码表存放在Map<Byte,String>
    static Map<Byte,String > huffmanCodes = new HashMap<>();
    //2.在生成赫夫曼编码表时需要去拼接路径，定义一个StringBuilder，存储某个叶子节点的节点路径
    static StringBuilder stringBuilder = new StringBuilder();

    //为了调用方便，重载getCodes
    private static Map<Byte,String> getCodes(Node root){
        if (root == null){
            return null;
        }
        //处理root的左子树
        getCodes(root.left,"0",stringBuilder);
        //处理root的右子树
        getCodes(root.right,"1",stringBuilder);
        return huffmanCodes;
    }
    /**
     * 功能：将传入的node节点的所有叶子节点的赫夫曼编码得到，并放入huffmanCode集合
     * @param node 传入节点
     * @param code 路径：左子节点是0，右子节点是1
     * @param stringBuilder 用于拼接路径
     */
    private static void getCodes(Node node,String code,StringBuilder stringBuilder){
        StringBuilder stringBuilder1 = new StringBuilder(stringBuilder);

        //将code加入到StringBuilder1
        stringBuilder1.append(code);
        if (node != null){
            //判断当前node是叶子节点，还是非叶子节点
            if (node.data == null){ //非叶子节点
                //递归处理
                //向左
                getCodes(node.left,"0",stringBuilder1);
                //向右
                getCodes(node.right,"1",stringBuilder1);
            }else { //说明是叶子节点
                //表示找到叶子节点
                huffmanCodes.put(node.data,stringBuilder1.toString());
            }
        }
    }

    //前序遍历方法
    private static void preOrder(Node root){
        if (root != null){
            root.preOder();
        }else {
            System.out.println("赫夫曼树为空");
        }
    }

    private static List<Node> getNodes(byte[] bytes){
        //创建一个ArrayList
        ArrayList<Node> nodes = new ArrayList<>();

        //存储每一个byte出现的次数
        Map<Byte,Integer> counts = new HashMap<>();
        for (byte b:bytes
             ) {
            Integer count = counts.get(b);
            if (count == null){
                counts.put(b,1);
            }else {
                counts.put(b,count + 1);
            }
        }

        //把每个键值对转成Node对象，并加入nodes集合
        for (Map.Entry<Byte,Integer> entry: counts.entrySet()
             ) {
            nodes.add(new Node(entry.getKey(),entry.getValue()));
        }

        return nodes;
    }

    //通过存放的List创建对应的赫夫曼树
    private static Node ceateHuffmanTree(List<Node> nodes){

        while (nodes.size() > 1){
            //排序，从小到大
            Collections.sort(nodes);
            //取出第一个最小的二叉树
            Node leftNode = nodes.get(0);
            //取出第二个最小的二叉树
            Node rightNode = nodes.get(1);
            //创建一颗新的二叉树，它的根节点只有权值，没有data
            Node parent = new Node(null,leftNode.weight + rightNode.weight);
            parent.left = leftNode;
            parent.right = rightNode;

            //将已经处理的两颗二叉树从nodes中删除
            nodes.remove(leftNode);
            nodes.remove(rightNode);

            //将新的二叉树加入到nodes
            nodes.add(parent);
        }
        //nodes最后的节点就是赫夫曼树的根节点
        return nodes.get(0);
    }
}

//创建Node，带数据和权值
class Node implements Comparable<Node>{
    Byte data; // 存放数据本身，比如‘a’ =》97， ‘ ’ =》32
    int weight; //权值，表示字符出现的字数
    Node left;
    Node right;

    public Node(Byte data, int weight) {
        this.data = data;
        this.weight = weight;
    }

    @Override
    public int compareTo(Node o) {
        return this.weight - o.weight; // 从小到大排序
    }

    @Override
    public String toString() {
        return "Node{" +
                "data=" + data +
                ", weight=" + weight +
                '}';
    }

    //前序遍历
    public void preOder(){
        System.out.println(this);
        if (this.left != null){
            this.left.preOder();
        }
        if (this.right != null){
            this.right.preOder();
        }
    }
}
