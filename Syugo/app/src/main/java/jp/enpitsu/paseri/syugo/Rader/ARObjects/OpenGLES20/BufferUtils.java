package jp.enpitsu.paseri.syugo.Rader.ARObjects.OpenGLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by iyobe on 2016/10/26.
 */
public class BufferUtils {
    //float配列をFloatBufferに変換
    FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer fb= ByteBuffer.allocateDirect(array.length*4).order(
                ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }

    //byte配列をByteBufferに変換
    ByteBuffer makeByteBuffer(byte[] array) {
        ByteBuffer bb=ByteBuffer.allocateDirect(array.length).order(
                ByteOrder.nativeOrder());
        bb.put(array).position(0);
        return bb;
    }
}
