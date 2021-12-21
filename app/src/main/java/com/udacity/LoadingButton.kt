package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val LEFT_ARC_ALIGN = Utils.dp2px(resources, 16f)
    private val ARC_ALPHA_THICK = Utils.dp2px(resources, 3.0f)
    private val ARC_PROGRESS_THICK = Utils.dp2px(resources, 5.0f)

    private var widthSize = 0f
    private var heightSize = 0f

    private val default_normalColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private val default_downloadColor = ContextCompat.getColor(context,R.color.colorAccent)
    private val default_textSize = 16.0f
    private val default_textColor = ContextCompat.getColor(context,R.color.white)
    private val default_cornerRadius = 0f;

    private var buttonText = ""
    private var normal_Color = default_normalColor
    private var download_Color = default_downloadColor
    private var textSize = default_textSize
    private var textColor = default_textColor
    private var cornerRadius = default_cornerRadius;

    private val textPaint = Paint(ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var path = Path()
    private var textBound = Rect()
    private var rectF = RectF()

    private var progressArc = 0f
    private var progress = 0f

    private val valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        duration = 1000
        addUpdateListener {
            progressArc = animatedValue as Float
            invalidate()
        }
    }
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->

        when (new) {
            ButtonState.Completed -> {
                //Timber.d("ButtonState: Completed")
                isEnabled = true
                if (valueAnimator.isStarted)
                    valueAnimator.cancel()
                progress = 1f

            }
            ButtonState.Clicked -> {
                //Timber.d("ButtonState: Clicked")
                isEnabled = false
                progress = 0f
                if (!valueAnimator.isStarted)
                    valueAnimator.start()
                setState(ButtonState.Loading)
            }
            ButtonState.Loading -> {


            }
        }

        invalidate()
    }


    init {
        val attributes =
            context.theme.obtainStyledAttributes(attrs,
                R.styleable.LoadingButton, 0, 0)

        initByAttributes(attributes)
        initPainters()
    }

    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.clipPath(path);

        fillPaint.color = normal_Color.toInt()
        //draw background
        canvas?.drawRect(0f, 0f, widthSize, heightSize, fillPaint)
        //buttonState = ButtonState.Loading
        //progress = 0.5f
        if (buttonState == ButtonState.Loading)
        {
            fillPaint.color = download_Color.toInt()
            var wP = progress*widthSize;
            var hP = heightSize
            //draw progress
            canvas?.drawRect(0f, 0f, wP, hP, fillPaint)

            linePaint.strokeWidth = ARC_ALPHA_THICK
            linePaint.color = ColorUtils.setAlphaComponent(textColor.toInt(), 0x7F)

            var arc_size = 2*textSize
            var left_align = LEFT_ARC_ALIGN
            var arc_posx = rectF.left + left_align;
            var arc_posy = rectF.top + (rectF.height() - arc_size) / 2
            //draw alpha background arc
            canvas?.drawArc(arc_posx, arc_posy, arc_posx + arc_size, arc_posy + arc_size,
            0.0f, 360.0f, false, linePaint)

            linePaint.strokeWidth = ARC_PROGRESS_THICK
            linePaint.color = textColor.toInt()
            //draw arc progress
            canvas?.drawArc(arc_posx, arc_posy, arc_posx + arc_size, arc_posy + arc_size,
                -90.0f, progressArc*360.0f, false, linePaint)

        }

        ///////drawText
        drawTextCentred(canvas, textPaint, buttonText,
            rectF.left + widthSize/2, rectF.top + heightSize/2)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)

        rectF = RectF(0f, 0f, widthSize, heightSize);
        resetPath()
    }

    private fun resetPath()
    {
        path.reset()
        path.addRoundRect(0f, 0f, widthSize, heightSize,
            cornerRadius, cornerRadius, Path.Direction.CW);
        path.close();
    }

    fun drawTextCentred(canvas: Canvas?, paint: Paint, text: String, cx: Float, cy: Float) {
        paint.getTextBounds(text, 0, text.length, textBound)
        canvas?.drawText(text, cx, cy - textBound.exactCenterY(), paint)
    }

    fun initByAttributes(attributes: TypedArray)
    {
        attributes.getString(R.styleable.LoadingButton_button_text)?.let {
            buttonText = it
        }
        normal_Color = attributes.getColor(R.styleable.LoadingButton_normal_color, default_normalColor.toInt()).toInt()
        download_Color = attributes.getColor(R.styleable.LoadingButton_download_color, default_downloadColor.toInt()).toInt()
        textSize = attributes.getDimension(R.styleable.LoadingButton_text_size, default_textSize)
        textColor = attributes.getColor(R.styleable.LoadingButton_text_color, default_textColor.toInt()).toInt()
        cornerRadius = attributes.getDimension(R.styleable.LoadingButton_corner_radius, default_cornerRadius)

    }

    fun initPainters()
    {
        textPaint.color = textColor.toInt()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize

        fillPaint.color = normal_Color.toInt()

        linePaint.color = textColor.toInt()
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeCap = Paint.Cap.ROUND
        linePaint.strokeJoin = Paint.Join.ROUND
        linePaint.strokeWidth = Utils.dp2px(resources, 1.0f)
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

    fun setText(text : String)
    {
        buttonText = text
    }

    fun setLoadingButton(text: String, progressVal : Float = 0f, state: ButtonState = ButtonState.Clicked)
    {
        buttonText = text;
        progress = progressVal
        buttonState = state
    }

}