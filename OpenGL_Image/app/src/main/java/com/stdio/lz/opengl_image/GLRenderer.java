package com.stdio.lz.opengl_image;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private int aPositionHandle;
    private int programId;

    private FloatBuffer vertexBuffer;private final float[] vertexData = {
            0f,0f,0f,
            1f,1f,0f,
            -1f,1f,0f,
            -1f,-1f,0f,
            1f,-1f,0f
    };

    private final short[] indexData = {
            0,1,2,
            0,2,3,
            0,3,4,
            0,4,1
    };

    private final float[] textureVertexData = {
            0.5f,0.5f,
            1f,0f,
            0f,0f,
            0f,1f,
            1f,1f
    };

    private final float[] projectionMatrix=new float[16];
    private int uMatrixHandle;

    private ShortBuffer indexBuffer;

    private FloatBuffer textureVertexBuffer;
    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;
    private int textureId;

    public  GLRenderer(Context context){
        this.context=context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexShader = Utilty.readRawTextFile(context, R.raw.vertex_shader);
        String fragmentShader= Utilty.readRawTextFile(context, R.raw.fragment_shader);
        programId=Utilty.createProgram(vertexShader,fragmentShader);
        aPositionHandle= GLES20.glGetAttribLocation(programId,"aPosition");
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(indexData.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indexData);
        indexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);

        uMatrixHandle=GLES20.glGetUniformLocation(programId,"uMatrix");

        uTextureSamplerHandle=GLES20.glGetUniformLocation(programId,"sTexture");
        aTextureCoordHandle=GLES20.glGetAttribLocation(programId,"aTexCoord");
        textureId=TextureHelper.loadTexture(context,R.raw.demo);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio=width>height?(float)width/height:(float)height/width;
        if (width>height){
            Matrix.orthoM(projectionMatrix,0,-ratio,ratio,-1f,1f,-1f,1f);
        }else{
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-ratio,ratio,-1f,1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixHandle,1,false,projectionMatrix,0);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexData.length,GLES20.GL_UNSIGNED_SHORT,indexBuffer);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES20.glVertexAttribPointer(aTextureCoordHandle,2,GLES20.GL_FLOAT,false,8,textureVertexBuffer);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
        GLES20.glUniform1i(uTextureSamplerHandle,0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexData.length,GLES20.GL_UNSIGNED_SHORT,indexBuffer);
    }

}
