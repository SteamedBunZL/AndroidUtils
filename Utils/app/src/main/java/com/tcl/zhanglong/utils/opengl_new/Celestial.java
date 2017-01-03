package com.tcl.zhanglong.utils.opengl_new;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

//��ʾ�ǿ��������
public class Celestial {
	final float UNIT_SIZE=6.0f;//����뾶
	private FloatBuffer   mVertexBuffer;//�����������ݻ���
	private IntBuffer   mColorBuffer;//������ɫ���ݻ���
    int vCount=0;//��������
    float yAngle;//������Y����ת�ĽǶ�
    int xOffset;//xƽ����
    int zOffset;//zƽ����
    float scale;//���ǳߴ�
    public Celestial(int xOffset,int zOffset,float scale,float yAngle,int vCount)
    {
    	this.xOffset=xOffset;
    	this.zOffset=zOffset;
    	this.yAngle=yAngle;
    	this.scale=scale;
    	this.vCount=vCount;
    	
    	//�����������ݵĳ�ʼ��================begin=======================================       
        float vertices[]=new float[vCount*3];//ÿһ������XYZ������������ʾ
        for(int i=0;i<vCount;i++)//�������vCount��λ�������ϵĵ�
        {
        	//�������ÿ�����ǵ�xyz����
        	double angleTempJD=Math.PI*2*Math.random();//������0~360�����������
        	double angleTempWD=Math.PI/2*Math.random();//γ����0~90�����������
        	vertices[i*3]=(float)(UNIT_SIZE*Math.cos(angleTempWD)*Math.sin(angleTempJD));//ͨ����ʽ���������϶�Ӧ��γ���ϵĵ������
        	vertices[i*3+1]=(float)(UNIT_SIZE*Math.sin(angleTempWD));
        	vertices[i*3+2]=(float)(UNIT_SIZE*Math.cos(angleTempWD)*Math.cos(angleTempJD));
        }
		
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
        
       //������ɫ���ݵĳ�ʼ��================begin============================
        final int one = 65535;
        int colors[]=new int[vCount*4];//������ɫֵ���飬ÿ������4��ɫ��ֵRGBA
        for(int i=0;i<vCount;i++)//�����е����ɫ���óɰ�ɫ��
        {
        	colors[i*4]=one;
        	colors[i*4+1]=one;
        	colors[i*4+2]=one;
        	colors[i*4+3]=0;
        }
        
        //����������ɫ���ݻ���
        //vertices.length*4����Ϊһ��int�������ĸ��ֽ�
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mColorBuffer = cbb.asIntBuffer();//ת��Ϊint�ͻ���
        mColorBuffer.put(colors);//�򻺳����з��붥����ɫ����
        mColorBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //������ɫ���ݵĳ�ʼ��================end============================
    }

    public void drawSelf(GL10 gl)
    {        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);//���ö�����������
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);//���ö�����ɫ����
        
        gl.glDisable(GL10.GL_LIGHTING);//��������� 
        gl.glPointSize(scale);//�������ǳߴ�
        gl.glPushMatrix();//�����任����
        gl.glTranslatef(xOffset*UNIT_SIZE, 0, 0);//x��ƫ��
        gl.glTranslatef(0, 0, zOffset*UNIT_SIZE);//y��ƫ��
        gl.glRotatef(yAngle, 0, 1, 0);//y����ת
        
		//Ϊ����ָ��������������
        gl.glVertexPointer
        (
        		3,				//ÿ���������������Ϊ3  xyz 
        		GL10.GL_FLOAT,	//��������ֵ������Ϊ GL_FIXED
        		0, 				//����������������֮��ļ��
        		mVertexBuffer	//������������
        );        
        
        //Ϊ����ָ��������ɫ����
        gl.glColorPointer
        (
        		4, 				//������ɫ����ɳɷ֣�����Ϊ4��RGBA
        		GL10.GL_FIXED, 	//������ɫֵ������Ϊ GL_FIXED
        		0, 				//����������ɫ����֮��ļ��
        		mColorBuffer	//������ɫ����
        );
		
        //���� ��
        gl.glDrawArrays
        (
        		GL10.GL_POINTS, 		//�Ե㷽ʽ���
        		0, 			 			//��ʼ����
        		vCount					//���������
        );
        
        gl.glPopMatrix();//�ָ��任����
        gl.glPointSize(1);//�ָ����سߴ�
        gl.glEnable(GL10.GL_LIGHTING);//�������        
    }
}
