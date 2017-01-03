package com.tcl.zhanglong.utils.opengl_new;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
//��ʾ����������
public class Pyramid {
	final float UNIT_SIZE=0.5f;
	private FloatBuffer   mVertexBuffer;//�����������ݻ���
    private FloatBuffer   mNormalBuffer;//������ɫ���ݻ���
    private FloatBuffer mTextureBuffer;//�����������ݻ���
    int vCount=0;//��������
    float yAngle;//��y��ת���ĽǶ�
    int xOffset;//xƽ����
    int zOffset;//yƽ����
    int texId;//����ID
    public Pyramid(int xOffset,int zOffset,float scale,float yAngle,int texId)
    {
    	this.xOffset=xOffset;
    	this.zOffset=zOffset;
    	this.yAngle=yAngle;
    	this.texId=texId;
    	
    	//�����������ݵĳ�ʼ��================begin============================
        vCount=12;//ÿ��������4���������棬12������
        
        float vertices[]=new float[]
        {
        	0,2*scale*UNIT_SIZE,0,        	
        	UNIT_SIZE*scale,0,UNIT_SIZE*scale,
        	UNIT_SIZE*scale,0,-UNIT_SIZE*scale,
        	
        	0,2*scale*UNIT_SIZE,0,        	
        	UNIT_SIZE*scale,0,-UNIT_SIZE*scale,
        	-UNIT_SIZE*scale,0,-UNIT_SIZE*scale,
        	
        	0,2*scale*UNIT_SIZE,0,        	
        	-UNIT_SIZE*scale,0,-UNIT_SIZE*scale,
        	-UNIT_SIZE*scale,0,UNIT_SIZE*scale,
        	
        	0,2*scale*UNIT_SIZE,0,        	
        	-UNIT_SIZE*scale,0,UNIT_SIZE*scale,
        	UNIT_SIZE*scale,0,UNIT_SIZE*scale, 
        };
		
        //���������������ݻ���
        //vertices.length*4����Ϊһ��Float�ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊint�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //�����������ݵĳ�ʼ��================end============================
        
        //���㷨�������ݵĳ�ʼ��================begin============================
        float normals[]=new float[]
        {
        	0.89443f,0.44721f,0f,
        	0.89443f,0.44721f,0f,
        	0.89443f,0.44721f,0f,
        	
        	0,0.44721f,-0.89443f,
        	0,0.44721f,-0.89443f,
        	0,0.44721f,-0.89443f,
        	
        	-0.89443f,0.44721f,0f,
        	-0.89443f,0.44721f,0f,
        	-0.89443f,0.44721f,0f,
        	
        	0,0.44721f,0.89443f,
        	0,0.44721f,0.89443f,
        	0,0.44721f,0.89443f,
        };

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊint�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥����ɫ����
        mNormalBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //������ɫ���ݵĳ�ʼ��================end============================
        
        //���� �������ݳ�ʼ��
        float[] texST=
        {
        	0.5f,0.0f,0,1,1,1,
        	0.5f,0.0f,0,1,1,1,
        	0.5f,0.0f,0,1,1,1,
        	0.5f,0.0f,0,1,1,1,
        };
        ByteBuffer tbb = ByteBuffer.allocateDirect(texST.length*4);
        tbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTextureBuffer = tbb.asFloatBuffer();//ת��Ϊint�ͻ���
        mTextureBuffer.put(texST);//�򻺳����з��붥����ɫ����
        mTextureBuffer.position(0);//���û�������ʼλ��         
    }

    public void drawSelf(GL10 gl)
    {        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);//���ö�����������
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        
        gl.glPushMatrix();//�����任�����ֳ�
        gl.glTranslatef(xOffset*UNIT_SIZE, 0, 0);//xƽ��
        gl.glTranslatef(0, 0, zOffset*UNIT_SIZE);//yƽ��
        gl.glRotatef(yAngle, 0, 1, 0);//��y��ת
        
		//Ϊ����ָ��������������
        gl.glVertexPointer
        (
        		3,				//ÿ���������������Ϊ3  xyz 
        		GL10.GL_FLOAT,	//��������ֵ������Ϊ GL_FIXED
        		0, 				//����������������֮��ļ��
        		mVertexBuffer	//������������
        );
        
        //Ϊ����ָ�����㷨��������
        gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
        
        //��������
        gl.glEnable(GL10.GL_TEXTURE_2D);   
        //����ʹ������ST���껺��
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        //Ϊ����ָ������ST���껺��
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        //�󶨵�ǰ����
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
		
		
        //����ͼ��
        gl.glDrawArrays
        (
        		GL10.GL_TRIANGLES, 		//�������η�ʽ���
        		0, 			 			//��ʼ����
        		vCount					//���������
        );
        
        gl.glPopMatrix();//�ָ��任�����ֳ�
    }
}
