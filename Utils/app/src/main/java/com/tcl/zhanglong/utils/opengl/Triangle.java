package com.tcl.zhanglong.utils.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Steve on 16/12/20.
 */

public class Triangle {

    private IntBuffer myVertexBuffer;//顶点坐标数据缓冲
    private IntBuffer myColorBuffer;//顶点着色数据缓冲
    private ByteBuffer myIndexBuffer;//顶点构建的索引数据缓冲

    int vCount = 0;//顶点数量
    int iCount = 0;//索引数量

    float yAngle = 0;//
    float zAngle = 0;//

    public Triangle() {
        vCount = 3;//
        final int UNIT_SIZE = 10000;//
        int[] vertices = new int[]{
                -8*UNIT_SIZE,6*UNIT_SIZE,0,
                -8*UNIT_SIZE,-6*UNIT_SIZE,0,
                8*UNIT_SIZE,-6*UNIT_SIZE,0
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//
        vbb.order(ByteOrder.nativeOrder());//
        myVertexBuffer = vbb.asIntBuffer();//
        myVertexBuffer.put(vertices);
        myVertexBuffer.position(0);
        final int one = 65535;
        int[] colors = new int[]{
                one,one,one,0,
                one,one,one,0,
                one,one,one,0
        };
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);//
        cbb.order(ByteOrder.nativeOrder());//
        myColorBuffer = cbb.asIntBuffer();//
        myColorBuffer.put(colors);//
        myColorBuffer.position(0);

        iCount = 3;
        byte[] indices = new byte[]{
            0,1,2
        };

        myIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        myIndexBuffer.put(indices);
        myIndexBuffer.position(0);


    }

    public void drawSelf(GL10 gl){
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glRotatef(yAngle,0,1,0);
        gl.glRotatef(zAngle,0,0,1);
        gl.glVertexPointer(
                3,
                GL10.GL_FIXED,
                0,
                myVertexBuffer
        );
        gl.glColorPointer(
                4,
                GL10.GL_FIXED,
                0,
                myColorBuffer
        );
        gl.glDrawElements(
                GL10.GL_TRIANGLES,
                iCount,
                GL10.GL_UNSIGNED_BYTE,
                myIndexBuffer
        );
    }
}
