# **实现效果**
	1、可以根据子View的宽度自动换行
	2、子View的高度超过layout的大小时可以滑动
	3、根据需要设置子View的Gravity
	4、如果需要，可以使用LayoutTransition设置子View添加删除时的动画效果
![see com.example.xing.androidlearning.Activity.FlowLayoutActivity](http://img.blog.csdn.net/20160824161329490)
see com.example.xing.androidlearning.Activity.FlowLayoutActivity


#**实现自动换行以及可自定义Gravity**

实现一个可以自动换行的Flowlayout。
##1、onMeasure
遍历子View测量大小，算出每一行的宽度以及总的高度和宽度。
	当对齐方式不是 top|left 时需要根据总的高度和每一行的宽度来决定子View应该放置在哪里；当layout的宽高设置是wrap_contant时可以根据总宽高设置大小。
    累加子View的宽度，如果超过layout的宽度，判断为需要换行，将当前行中子View的最大高度记为当前行的高度，总高度累加。
    代码：
``` java

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mLineWidthList.clear();
        totalLineHeight = 0;
        int totalWidth = 0;

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //去掉padding和margin后实际的宽和高
        int parentUsableWidth = sizeWidth - getPaddingLeft() - getPaddingRight();
        int parentUsableHeight = sizeHeight - getPaddingTop() - getPaddingBottom();
        Log.e(TAG, "parentUsableWidth: " + parentUsableWidth + ", parentUsableHeight: " + parentUsableHeight);

        int currentLineWidth = 0;   
        int currentLineHeight = 0;

        int childCount = getChildCount();
        if (childCount <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //测量子View
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            Log.d(TAG, String.format("measureChildWithMargins(%s,%s,%s,%s)", widthMeasureSpec, currentLineWidth, heightMeasureSpec, totalLineHeight));

            //带Margin的
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            Log.d(TAG, "childWidth: " + childWidth + ", childHeight: " + childHeight +
                    String.format(", child margin:%s,%s,%s,%s", lp.leftMargin, lp.rightMargin, lp.topMargin, lp.bottomMargin));
            if (currentLineWidth + childWidth > parentUsableWidth) {
                //换行处理
                Log.v(TAG, "换行," + (currentLineWidth + childWidth) + "," + parentUsableWidth);
                totalLineHeight += currentLineHeight;   //累加高度
                mLineWidthList.add(currentLineWidth);   //记录当前行宽
                totalWidth = Math.max(currentLineWidth, totalWidth);
                currentLineWidth = childWidth;
                currentLineHeight = childHeight;
            } else {
                currentLineWidth += childWidth;
                currentLineHeight = Math.max(childHeight, currentLineHeight);
            }

            //遍历到最后一个ChildView时未换行, 单独处理
            if (i == childCount - 1) {
                Log.v(TAG, "换行," + (currentLineWidth + childWidth) + "," + parentUsableWidth);
                totalLineHeight += currentLineHeight;
                mLineWidthList.add(currentLineWidth);
                totalWidth = Math.max(currentLineWidth, totalWidth);
            }

            //根据需要，是否要将子View总的宽高设为layout的宽高
            setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : totalWidth + getPaddingLeft() + getPaddingRight(),
                    modeHeight == MeasureSpec.EXACTLY ? sizeHeight : totalLineHeight + getPaddingTop() + getPaddingBottom());
        }
    }
```
##2、onLayout
使用onMeasure的测量结果根据设定的对齐方式在正确的位置放置子View。
###1）对齐方式
对齐方式在attrs.xml里面使用枚举类型，安卓的Gravity类中几个值为：
```java
	CENTER = 17;           //0001 0001
	CENTER_VERTICAL = 16;  //0001 0000
	CENTER_HORIZONTAL = 1; //0000 0001
	LEFT = 3;              //0000 0011
	RIGHT = 5;             //0000 0101
	BOTTOM = 80;           //0101 0000
	TOP = 48;              //0011 0000
```
attrs.xml里面这么写
```xml
    <declare-styleable name="flowlayout">
        <!--<attr name="child_gravity" format="integer"/>-->
        <attr name="child_gravity">
            <enum name="center" value="17" />
            <enum name="center_vertical" value="16" />
            <enum name="center_horizontal" value="1" />
            <enum name="left" value="3" />
            <enum name="right" value="5" />
            <enum name="top" value="48" />
            <enum name="bottom" value="80" />
        </attr>
    </declare-styleable>
```
可以看出，只要将在布局xml的标签属性中的到的实际的gravity值与Gravity类中这些值相与，判断结果和这些值相不相等就可以知道设置的是哪个值了。
从设计的值和实际道理上可以看出，CENTER_VERTICAL跟BOTTOM、TOP会冲突，而且CENTER的优先级肯定是要低的，只要设置了bottom或是top，垂直居中就会无效。
处理的代码入下：
```java
    /**
     * 根据对齐方式计算当前行最左上角那个点的坐标
     * @param lineWidth 当前行宽
     * @return Point x代表当前行的起始的横坐标(left)，y代表当前行顶部的坐标(top)
     */
    private Point getCurrentTopLeft(int parentLeft, int parentTop, int parentHeight, int parentWidth, int lineWidth) {
        Log.d(TAG, String.format("getCurrentTopLeft(%s,%s,%s,%s,%s) ", parentLeft, parentTop, parentHeight, parentWidth, lineWidth) + "totalLineHeight:" + totalLineHeight);
        Point point = new Point(getPaddingLeft(), getPaddingTop());
        if ((mChildGravity & Gravity.CENTER) == Gravity.CENTER) {
            Log.d(TAG, "child gravity = CENTER");
            point.x = (parentWidth - lineWidth) / 2;
            point.y = (parentHeight - totalLineHeight) / 2;
        }

        if ((mChildGravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
            Log.d(TAG, "child gravity = CENTER_VERTICAL");
            point.y = (parentHeight - totalLineHeight) / 2;
        }
        if ((mChildGravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
            Log.d(TAG, "child gravity = CENTER_HORIZONTAL");
            point.x = (parentWidth - lineWidth) / 2;
        }
        if ((mChildGravity & Gravity.LEFT) == Gravity.LEFT) {
            Log.d(TAG, "child gravity = LEFT");
            point.x = getPaddingLeft();
        }
        if ((mChildGravity & Gravity.RIGHT) == Gravity.RIGHT) {
            Log.d(TAG, "child gravity = RIGHT");
            point.x = parentWidth - getPaddingLeft() - lineWidth;
        }
        if ((mChildGravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            Log.d(TAG, "child gravity = BOTTOM");
            point.y = parentHeight - totalLineHeight;
        }
        if ((mChildGravity & Gravity.TOP) == Gravity.TOP) {
            Log.d(TAG, "child gravity = TOP");
            point.y = getPaddingTop();
        }
        return point;
    }
```
默认对齐为左上，所以point初始为（getPaddingLeft(), getPaddingTop()）,处理垂直方向上的对齐是设置point.y, 水平就point.x。最后可以得到子View们整体的左上角的起始点。dian
###2）放置子View
得到了每一行最左上角的点之后，就可以根据那个点来从左到右放置子View了：
```java
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //获得在onMeasure方法中计算后得到的这个FlowLayout的宽度, 用的currentRight包含了getPaddingLeft，所以可用大小要加回去
        int parentUsableWidth = getWidth() - getPaddingLeft() - getPaddingRight() + getPaddingLeft();
        Log.e(TAG, "layout parentUsableWidth: " + parentUsableWidth);

        int childCount = getChildCount();
        if (childCount <= 0) {
            //super是个抽象方法。。
            return;
        }

        int currLine = 0; //当前行游标
        Point point = getCurrentTopLeft(getLeft(), getTop(), getHeight(), getWidth(), mLineWidthList.get(currLine++));
        int currentRight = point.x; //代表左上角点的两个坐标
        int currentTop = point.y;

        int currentLineHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (currentRight + childWidth > parentUsableWidth) {
                //换行处理
                currentTop += currentLineHeight;
                currentRight = getCurrentTopLeft(getLeft(), getTop(), getHeight(), getWidth(), mLineWidthList.get(currLine++)).x;
                currentLineHeight = 0;
            }

            Log.d(TAG, "child layout, top: " + currentTop + ", left: " + currentRight);
            childView.layout(currentRight + lp.leftMargin, currentTop + lp.topMargin,
                    currentRight + childWidth - lp.rightMargin, currentTop + childHeight - lp.bottomMargin);
            //为下一个view的layout设置当前状态
            currentLineHeight = Math.max(currentLineHeight, childHeight);
            currentRight += childWidth;
        }

        //纠正一下子View的整体位置，当有上下滑动过，纠正位移是必要的
        if (isViewAdded || isViewRemoved) {
            correctScrollY();
            isViewAdded = false;
            isViewRemoved = false;
        }
    }
```
#**实现可滑动 Scrollable**
##1、控制触摸事件的分发
如果手指滑动的垂直方向超过一定距离的话判断为需要scrollY并且阻止传递给子View，如果不是，触摸事件正常传递。滑动距离的阈值可以这样获得：`int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();`
```java
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent, ACTION_DOWN");
                mLastMotionY = ev.getY();
                scrollEnable = false;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent, ACTION_MOVE");
                if (Math.abs(mLastMotionY - ev.getY()) > touchSlop) {
                    Log.d(TAG, "onInterceptTouchEvent, return true");
                    return true;    //不传递
                }
        }
        return false;
    }
```
##2、滑动
滑动时，上下滑动不能无限制的任意上滑下滑，
当子View的总高度没有超出layout的高度时不能滑动（或者可以滑，但是松手后要纠正scrollY），
当第一个子View的top比layout的top低时在松手后要纠正一下scrollY，最后一个子View的bottom也要注意。
onTouchEvent:
```java
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        
        float y = event.getY();
        int firstChildTop = getChildAt(0).getTop();
        int lastChildBottom = getChildAt(getChildCount() - 1).getBottom();
        if (firstChildTop > 0 && lastChildBottom < getHeight()) {   //子View没有超出layout，不滑动
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent, ACTION_DOWN");
                mLastMotionY = y; //记住开始落下的点
                break;
            case MotionEvent.ACTION_MOVE:
                int detaY = (int) (mLastMotionY - y);
                Log.d(TAG, "onTouchEvent ACTION_MOVE, detaY=" + detaY + ", touchSlop=" + touchSlop);
                if (scrollEnable || Math.abs(detaY) > touchSlop) {
                    scrollBy(0, detaY);
                    mLastMotionY = y;
                    scrollEnable = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent ACTION_UP, firstChildTop=" + firstChildTop +
                        ", lastChildBottom=" + lastChildBottom +
                        ", ScrollY=" + getScrollY());
                Point point = getCurrentTopLeft(getLeft(), getTop(), getHeight(), getWidth(), mLineWidthList.get(0));
                if (firstChildTop - getScrollY() > 0) { //第一个子View的顶部在layout的top下
                    scrollTo(0, point.y);
                } else if (lastChildBottom - getScrollY() < getHeight()) {  //最后一个子View的底部在layout的bottom上
                    scrollTo(0, totalLineHeight - getHeight() + point.y);
                }
        }
        return true;
    }
```
纠正ScrollY的方法：
```java
    /**
     * 在滑动过之后，添加或者删除View之后子View整体的Y位移需要调整
     *     要在onLayout之后用，不然拿到的top跟bottom是添加之前的值
     */
    void correctScrollY() {
        int firstChildTop = getChildAt(0).getTop();
        int lastChildBottom = getChildAt(getChildCount() - 1).getBottom();
        Log.d(TAG, "correctScrollY , firstChildTop=" + firstChildTop +
                ", lastChildBottom=" + lastChildBottom);
        if (firstChildTop > 0 && lastChildBottom < getHeight()) {
            Log.e(TAG, "correctScrollY");
            scrollTo(0, 0);
        } else {
            Point p = getCurrentTopLeft(getLeft(), getTop(), getHeight(), getWidth(), mLineWidthList.get(0));
            if (isViewAdded) {
	            //添加View时滑动到底部（因为新View总是在底部）
                scrollTo(0, totalLineHeight - getHeight() + p.y);
            } else if (isViewRemoved && lastChildBottom - getScrollY() < getHeight()) {
            //删除了View后，如果最后一个子View的底部跟layout的底部之间有空隙，
            //就让最后一个子View的底部跟layout的底部对齐
                scrollTo(0, totalLineHeight - getHeight() + p.y);
            }
        }
    }
```

#**添加删除View时的动画**
在构造函数中这样：
```java
        mLayoutTransition = new LayoutTransition();
        this.setLayoutTransition(mLayoutTransition);
```
然后就通过LayoutTransition设置动画。动画应该简洁，仅仅起到不要让view出来的太突兀的作用就好。


源码看https://github.com/xing2387/AndroidLearning
