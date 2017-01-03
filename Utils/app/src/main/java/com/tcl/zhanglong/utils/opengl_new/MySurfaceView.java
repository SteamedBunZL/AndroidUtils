package com.tcl.zhanglong.utils.opengl_new;

import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tcl.zhanglong.utils.R;

class MySurfaceView extends GLSurfaceView {

    static int wallaId;//ǽ������a��ID
    static int wallbId;//ǽ������b��ID
    static int wallcId;//ǽ������c��ID
    static int desertId;//ɳĮ����ID
	
	private SceneRenderer mRenderer;//������Ⱦ��	
	
	public MySurfaceView(Context context) {
        super(context);
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
    }

	private class SceneRenderer implements Renderer
    {   
    	Pyramid[] pArray;//����������
    	Desert desert;//ɳĮ
    	Celestial celestialSmall;//С�����ǿհ���
    	Celestial celestialBig;//�������ǿհ���
    	
    	double lightAngle=120.0;//����ĽǶ�
    	
    	public SceneRenderer()
    	{
                 new Thread()
                 {//��ʱ��ת������߳�
                	 public void run()
                	 {
                		 while(true)
                		 {
                			 lightAngle+=0.5;
                			 if(lightAngle>=360)
                			 {
                				 lightAngle=0;
                			 }
                			 try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
                		 }
                	 }
                 }.start();
    	}
    	
        public void onDrawFrame(GL10 gl) {            
            //ʹ��ƽ����ɫ
        	gl.glShadeModel(GL10.GL_SMOOTH);    
        	//�趨Sun��Դ��λ��
        	float lxSun=(float)(1*Math.cos(Math.toRadians(lightAngle)));
        	float lySun=(float)(1*Math.sin(Math.toRadians(lightAngle)));
        	float[] positionParamsGreen={lxSun,lySun,0.6f,0};//����0��ʾʹ�ö����
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, positionParamsGreen,0); 
        	
        	//�����ɫ��������Ȼ���
        	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        	//���õ�ǰ����Ϊģʽ����
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            //���õ�ǰ����Ϊ��λ����
            gl.glLoadIdentity();
            
            //���ƽ�����
            for(Pyramid tp:pArray)
            {
            	tp.drawSelf(gl);
            }        
            //����ɳĮ
            desert.drawSelf(gl);
            //�����ǿ�
            celestialSmall.drawSelf(gl);
            celestialBig.drawSelf(gl);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ�� 
        	gl.glViewport(0, 0, width, height);
        	//���õ�ǰ����ΪͶӰ����
            gl.glMatrixMode(GL10.GL_PROJECTION);
            //���õ�ǰ����Ϊ��λ����
            gl.glLoadIdentity();
            //����͸��ͶӰ�ı���
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            gl.glFrustumf(-ratio, ratio, -0.5f, 1.5f, 1, 100);   
            
            //����cameraλ��
            GLU.gluLookAt
            (
            		gl, 
            		-1.0f,   //����λ�õ�X
            		0.6f, 	//����λ�õ�Y
            		3.0f,   //����λ�õ�Z
            		0, 	//�����򿴵ĵ�X
            		0.2f,   //�����򿴵ĵ�Y
            		0,   //�����򿴵ĵ�Z
            		0, 
            		1, 
            		0
            );   
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //�رտ����� 
        	gl.glDisable(GL10.GL_DITHER);
        	//�����ض�Hint��Ŀ��ģʽ������Ϊ����Ϊʹ�ÿ���ģʽ
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);
            //������Ļ����ɫ��ɫRGBA
            gl.glClearColor(0,0,0,0);            
            //������Ȳ���
            gl.glEnable(GL10.GL_DEPTH_TEST);
            //����Ϊ�򿪱������
    		gl.glEnable(GL10.GL_CULL_FACE);
            
            //��ʼ������
            wallaId=initTexture(gl, R.drawable.walla);
            wallbId=initTexture(gl,R.drawable.wallb);
            wallcId=initTexture(gl,R.drawable.wallc);
            desertId=initTexture(gl,R.drawable.desert);
            
            //����������
            pArray=new Pyramid[]
	        {
	    			new Pyramid(-2,-2,2.0f,30,wallaId),
	    			new Pyramid(3,-7,2.0f,0,wallbId),
	    			new Pyramid(6,-2,2.0f,0,wallcId),
	        };
            //����ɳĮ
            desert=new Desert(-20,-20,4,0,desertId,40,40);
            //�����ǿ�
            celestialSmall=new Celestial(0,0,1,0,250);
            celestialBig=new Celestial(0,0,2,0,50);
            
            gl.glEnable(GL10.GL_LIGHTING);//�������        
            initSunLight(gl);//��ʼ�������Դ
            initMaterial(gl);//��ʼ������
            
            gl.glEnable(GL10.GL_FOG);//������
            initFog(gl);//��ʼ����
            
            new Thread()
            {//��ʱת���ǿյ��߳�
           	 public void run()
           	 {
           		 while(true)
           		 {
           			celestialSmall.yAngle+=0.5;
           			if(celestialSmall.yAngle>=360)
           			{
           				celestialSmall.yAngle=0;
           			}
           			celestialBig.yAngle+=0.5;
          			if(celestialBig.yAngle>=360)
          			{
          				celestialBig.yAngle=0;
          			}
           			 try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
           		 }
           	 }
            }.start();
        }
    }
	
	private void initMaterial(GL10 gl)
	{//����Ϊ��ɫʱʲô��ɫ�Ĺ���������ͽ����ֳ�ʲô��ɫ
        //������Ϊ��ɫ����
        float ambientMaterial[] = {0.4f, 0.4f, 0.4f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambientMaterial,0);
        //ɢ���Ϊ��ɫ����
        float diffuseMaterial[] = {0.8f, 0.8f, 0.8f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuseMaterial,0);
        //�߹����Ϊ��ɫ
        float specularMaterial[] = {0.6f, 0.6f, 0.6f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specularMaterial,0);
        //�߹ⷴ������,��Խ���������ԽСԽ��
        float shininessMaterial[] = {1.5f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininessMaterial,0);
	}
	
	private void initSunLight(GL10 gl)
	{
        gl.glEnable(GL10.GL_LIGHT0);//��0�ŵ�  
        
        //����������
        float[] ambientParams={0.2f,0.2f,0.0f,1.0f};//����� RGBA
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientParams,0);            

        //ɢ�������
        float[] diffuseParams={1f,1f,0f,1.0f};//����� RGBA
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseParams,0); 
        
        //���������
        float[] specularParams={1.0f,1.0f,0.0f,1.0f};//����� RGBA
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularParams,0);          
	}
	
	//��ʼ����
	public void initFog(GL10 gl)
	{
		float[] fogColor={1,0.91765f,0.66667f,0};//�����ɫ
		gl.glFogfv(GL10.GL_FOG_COLOR, fogColor, 0);//���������ɫ��RGBAģʽ
		gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_EXP2);//�������ģʽ��ѡ��ͬ��������GL_EXP(Ĭ��)��GL_EXP2��GL_LINEAR
		gl.glFogf(GL10.GL_FOG_DENSITY, 1);//�������Ũ�ȣ�Ũ�ȷ�ΧΪ0~1֮�䣬0��ʾ���Ũ�ȣ�1��ʾ���Ũ�ȡ�
		gl.glFogf(GL10.GL_FOG_START, 0.5f);//������Ŀ�ʼ���룬�����������������롣
		gl.glFogf(GL10.GL_FOG_END, 100.0f);//������Ľ������룬�������������Զ���롣
	}
	
	//��ʼ������
	public int initTexture(GL10 gl,int drawableId)//textureId
	{
		//��������ID
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);    
		int currTextureId=textures[0];    
		gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,GL10.GL_CLAMP_TO_EDGE);
        
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp; 
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle(); 
        
        return currTextureId;
	}
}
