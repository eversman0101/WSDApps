package com.sunday.slidetabfragment.blue;

/**
 * 协议标识：蓝牙通道读取一帧数据时，用协议标识来确定读取的字节长度
 *  
 *
 * @author ShiPengHao
 * @date 2018/1/12
 */
public interface ProtocolSign {
    /**
     * 获取协议标识的字节长度
     * @return 协议标识长度
     */
    int getSignLen();

    /**
     * 根据协议标识的头部，确定剩余需要读取的字节长度
     * @param head 协议标识头
     * @return 剩余需要读取的字节长度
     */
    int getBodyLen(byte[] head);

    /**
     * 检查{@link #getSignLen}字节的数据是否是本协议的标识
     * @param head 协议标识头
     * @return 是true，否则false
     */
    boolean checked(byte[] head);

    /**
     * 检查此帧是否是心跳帧
     * @param frame 此帧全部报文
     * @return 是true，否则false
     */
    boolean filterHeartFrame(byte[] frame);
    /*
     * 过滤蓝牙模块初始化时的报文
     * */
    boolean filterModelFrame(byte[] frame);
}