package floatwindow.xishuang.float_lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import floatwindow.xishuang.float_lib.FloatActionController;
import floatwindow.xishuang.float_lib.R;

/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:悬浮窗的布局
 */
public class FloatLayout extends FrameLayout {
    private final WindowManager mWindowManager;
    private final ImageView mFloatView;
    private final DraggableFlagView mDraggableFlagView;
    private long startTime;
    private float mTouchStartX;
    private float mTouchStartY;
    private boolean isclick;
    //    private boolean isLongclick;
    private WindowManager.LayoutParams mWmParams;
    private Context mContext;
    private long endTime;
    private OnClickListener onClickListener;
    private OnLongClickListener mLongClickListener;

    //是否移动了
    private boolean isMoved;
    //是否释放了
    private boolean isReleased;
    //长按的runnable
    private Runnable mLongPressRunnable;
    //计数器，防止多次点击导致最后一次形成longpress的时间变短
    private int mCounter;

    public FloatLayout(Context context){
        this(context,null);
        mContext = context;
    }

    public FloatLayout(Context context,AttributeSet attrs){
        super(context,attrs);
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_littlemonk_layout,this);
        //浮动窗口按钮
        mFloatView = (ImageView)findViewById(R.id.float_id);
        mDraggableFlagView = (DraggableFlagView)findViewById(R.id.main_dfv);
        mDraggableFlagView.setOnDraggableFlagViewListener(new DraggableFlagView.OnDraggableFlagViewListener() {
            @Override
            public void onFlagDismiss(DraggableFlagView view){
                //小红点消失的一些操作
            }
        });
        FloatActionController.getInstance().setObtainNumber(0);

        mLongPressRunnable = new Runnable() {

            @Override
            public void run(){
                mCounter--;
                //计数器大于0，说明当前执行的Runnable不是最后一次down产生的。
                if (mCounter > 0 || isReleased || isMoved) return;

                if (mLongClickListener != null) {
                    mLongClickListener.onLongClick(FloatLayout.this);
                }
            }
        };


    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        //下面的这些事件，跟图标的移动无关，为了区分开拖动和点击事件
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                startTime = System.currentTimeMillis();
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();

                mCounter++;
                isReleased = false;
                isMoved = false;
                postDelayed(mLongPressRunnable,ViewConfiguration.getLongPressTimeout());

                break;
            case MotionEvent.ACTION_MOVE:
                //图标移动的逻辑在这里
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 10 && Math.abs(mTouchStartY - mMoveStartY) > 10) {
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int)(x - mTouchStartX);
                    mWmParams.y = (int)(y - mTouchStartY);
                    mWindowManager.updateViewLayout(this,mWmParams);
                    isMoved = true;
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();

                isReleased = true;

                // 当从点击到弹起小于100毫秒的时候,则判断为点击,如果超过则不响应点击事件
                isclick = !((endTime - startTime) > 0.1 * 1000L);

                break;
        }
        //响应点击事件
        if (isclick) {
            if (onClickListener != null) {
                onClickListener.onClick(this);
            }
        }

        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params){
        mWmParams = params;
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;

    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener){
        this.mLongClickListener = onLongClickListener;
    }

    /**
     * 设置小红点显示
     */
    public void setDragFlagViewVisibility(int visibility){
        mDraggableFlagView.setVisibility(visibility);
    }

    /**
     * 设置小红点数量
     */
    public void setDragFlagViewText(int number){
        mDraggableFlagView.setText(number + "");
    }


}
