package by.pzmandroid.mac.utils.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import by.pzmandroid.mac.databinding.ViewProgressBinding

class ProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var currentState = true
    private var binding: ViewProgressBinding? = null

    init {
        binding = ViewProgressBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding!!.root)
        isClickable = true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    fun updateProgressState(state: Boolean) {
        binding?.let {
            with(it) {
                currentState = state
                visibility = if (state) View.VISIBLE else View.GONE
                vpProgressWheel.visibility = View.VISIBLE
                vpProgressWheel.spin()
            }
        }

    }
}