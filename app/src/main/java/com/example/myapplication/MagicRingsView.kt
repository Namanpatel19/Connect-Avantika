package com.example.myapplication

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MagicRingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val renderer: MagicRingsRenderer

    // Dynamic parameters
    var baseRadius: Float = 0.35f
    var scaleRate: Float = 0.1f
    var opacity: Float = 1.0f
    var expansion: Float = 1.0f

    init {
        setEGLContextClientVersion(2)
        renderer = MagicRingsRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    private inner class MagicRingsRenderer : Renderer {
        private var program: Int = 0
        private lateinit var vertexBuffer: FloatBuffer

        private val startTime = System.currentTimeMillis()

        // Uniform locations
        private var uTimeLoc = -1
        private var uResolutionLoc = -1
        private var uColorLoc = -1
        private var uColorTwoLoc = -1
        private var uAttenuationLoc = -1
        private var uLineThicknessLoc = -1
        private var uBaseRadiusLoc = -1
        private var uRadiusStepLoc = -1
        private var uScaleRateLoc = -1
        private var uRingCountLoc = -1
        private var uOpacityLoc = -1
        private var uNoiseAmountLoc = -1
        private var uRotationLoc = -1
        private var uRingGapLoc = -1
        private var uFadeInLoc = -1
        private var uFadeOutLoc = -1
        private var uExpansionLoc = -1

        private val vertexShaderCode = """
            attribute vec4 vPosition;
            void main() {
                gl_Position = vPosition;
            }
        """.trimIndent()

        private val fragmentShaderCode = """
            precision highp float;
            uniform float uTime, uAttenuation, uLineThickness;
            uniform float uBaseRadius, uRadiusStep, uScaleRate;
            uniform float uOpacity, uNoiseAmount, uRotation, uRingGap;
            uniform float uFadeIn, uFadeOut, uExpansion;
            uniform vec2 uResolution;
            uniform vec3 uColor, uColorTwo;
            uniform int uRingCount;

            const float HP = 1.5707963;
            const float CYCLE = 3.45;

            float fade(float t) {
                if (t < uFadeIn) return smoothstep(0.0, uFadeIn, t);
                return 1.0 - smoothstep(uFadeOut, CYCLE - 0.2, t);
            }

            float ring(vec2 p, float ri, float cut, float t0, float px) {
                float t = mod(uTime + t0, CYCLE);
                float r = (ri + t / CYCLE * uScaleRate) * uExpansion;
                float d = abs(length(p) - r);
                float a = atan(abs(p.y), abs(p.x)) / HP;
                float th = max(1.0 - a, 0.5) * px * uLineThickness;
                float h = (1.0 - smoothstep(th, th * 1.5, d)) + 1.0;
                d += pow(cut * a, 3.0) * r;
                return h * exp(-uAttenuation * d) * fade(t);
            }

            void main() {
                float px = 1.0 / min(uResolution.x, uResolution.y);
                vec2 p = (gl_FragCoord.xy - 0.5 * uResolution.xy) * px;
                float cr = cos(uRotation), sr = sin(uRotation);
                p = mat2(cr, -sr, sr, cr) * p;
                
                vec3 c = vec3(0.0);
                float rcf = max(float(uRingCount) - 1.0, 1.0);
                for (int i = 0; i < 10; i++) {
                    if (i >= uRingCount) break;
                    float fi = float(i);
                    vec3 rc = mix(uColor, uColorTwo, fi / rcf);
                    float r_val = ring(p, uBaseRadius + fi * uRadiusStep, pow(uRingGap, fi), i == 0 ? 0.0 : 2.95 * fi, px);
                    c = mix(c, rc, vec3(r_val));
                }
                
                float n = fract(sin(dot(gl_FragCoord.xy + uTime * 100.0, vec2(12.9898, 78.233))) * 43758.5453);
                c += (n - 0.5) * uNoiseAmount;
                gl_FragColor = vec4(c, max(c.r, max(c.g, c.b)) * uOpacity);
            }
        """.trimIndent()

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(0f, 0f, 0f, 1f)
            
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
            
            program = GLES20.glCreateProgram().also {
                GLES20.glAttachShader(it, vertexShader)
                GLES20.glAttachShader(it, fragmentShader)
                GLES20.glLinkProgram(it)
            }

            uTimeLoc = GLES20.glGetUniformLocation(program, "uTime")
            uResolutionLoc = GLES20.glGetUniformLocation(program, "uResolution")
            uColorLoc = GLES20.glGetUniformLocation(program, "uColor")
            uColorTwoLoc = GLES20.glGetUniformLocation(program, "uColorTwo")
            uAttenuationLoc = GLES20.glGetUniformLocation(program, "uAttenuation")
            uLineThicknessLoc = GLES20.glGetUniformLocation(program, "uLineThickness")
            uBaseRadiusLoc = GLES20.glGetUniformLocation(program, "uBaseRadius")
            uRadiusStepLoc = GLES20.glGetUniformLocation(program, "uRadiusStep")
            uScaleRateLoc = GLES20.glGetUniformLocation(program, "uScaleRate")
            uRingCountLoc = GLES20.glGetUniformLocation(program, "uRingCount")
            uOpacityLoc = GLES20.glGetUniformLocation(program, "uOpacity")
            uNoiseAmountLoc = GLES20.glGetUniformLocation(program, "uNoiseAmount")
            uRotationLoc = GLES20.glGetUniformLocation(program, "uRotation")
            uRingGapLoc = GLES20.glGetUniformLocation(program, "uRingGap")
            uFadeInLoc = GLES20.glGetUniformLocation(program, "uFadeIn")
            uFadeOutLoc = GLES20.glGetUniformLocation(program, "uFadeOut")
            uExpansionLoc = GLES20.glGetUniformLocation(program, "uExpansion")

            val quadCoords = floatArrayOf(
                -1.0f,  1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                 1.0f, -1.0f, 0.0f,
                 1.0f,  1.0f, 0.0f
            )
            vertexBuffer = ByteBuffer.allocateDirect(quadCoords.size * 4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(quadCoords)
                    position(0)
                }
            }
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            GLES20.glUseProgram(program)

            val time = (System.currentTimeMillis() - startTime) / 1000f
            GLES20.glUniform1f(uTimeLoc, time)
            
            GLES20.glUniform3f(uColorLoc, 0.988f, 0.258f, 1.0f) // #fc42ff
            GLES20.glUniform3f(uColorTwoLoc, 0.258f, 0.988f, 1.0f) // #42fcff
            GLES20.glUniform1i(uRingCountLoc, 6)
            GLES20.glUniform1f(uAttenuationLoc, 10f)
            GLES20.glUniform1f(uLineThicknessLoc, 2f)
            GLES20.glUniform1f(uBaseRadiusLoc, baseRadius)
            GLES20.glUniform1f(uRadiusStepLoc, 0.1f)
            GLES20.glUniform1f(uScaleRateLoc, scaleRate)
            GLES20.glUniform1f(uOpacityLoc, opacity)
            GLES20.glUniform1f(uExpansionLoc, expansion)
            GLES20.glUniform1f(uNoiseAmountLoc, 0.1f)
            GLES20.glUniform1f(uRotationLoc, 0f)
            GLES20.glUniform1f(uRingGapLoc, 1.5f)
            GLES20.glUniform1f(uFadeInLoc, 0.7f)
            GLES20.glUniform1f(uFadeOutLoc, 0.5f)

            val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)
            GLES20.glDisableVertexAttribArray(positionHandle)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
            GLES20.glUniform2f(uResolutionLoc, width.toFloat(), height.toFloat())
        }

        private fun loadShader(type: Int, shaderCode: String): Int {
            return GLES20.glCreateShader(type).also { shader ->
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)
            }
        }
    }
}
