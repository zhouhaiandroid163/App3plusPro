package com.zjw.apps3pluspro.bleservice;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by https://blog.csdn.net/weixin_39550587/article/details/112079826
 * on 2021/5/14
 */
public class ThreadPoolService {
    /**
     * 线程池变量
     */
    private ThreadPoolExecutor mThreadPoolExecutor;
    private static volatile ThreadPoolService sInstance = null;
    /**
     * 线程池中的核心线程数，默认情况下，核心线程一直存活在线程池中，即便他们在线程池中处于闲置状态。
     * 除非我们将ThreadPoolExecutor的allowCoreThreadTimeOut属性设为true的时候，这时候处于闲置的核心         * 线程在等待新任务到来时会有超时策略，这个超时时间由keepAliveTime来指定。一旦超过所设置的超时时间，闲     * 置的核心线程就会被终止。
     * CPU密集型任务  N+1   IO密集型任务   2*N
     */
    private final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    /**
     * 线程池中所容纳的最大线程数，如果活动的线程达到这个数值以后，后续的新任务将会被阻塞。包含核心线程数+非*      * 核心线程数。
     */
    private final int MAXIMUM_POOL_SIZE = Math.max(CORE_POOL_SIZE, 10);
    /**
     * 非核心线程闲置时的超时时长，对于非核心线程，闲置时间超过这个时间，非核心线程就会被回收。
     * 只有对ThreadPoolExecutor的allowCoreThreadTimeOut属性设为true的时候，这个超时时间才会对核心线       * 程产生效果。
     */
    private final long KEEP_ALIVE_TIME = 2;
    /**
     * 用于指定keepAliveTime参数的时间单位。
     */
    private final TimeUnit UNIT = TimeUnit.SECONDS;
    /**
     * 线程池中保存等待执行的任务的阻塞队列
     * ArrayBlockingQueue  基于数组实现的有界的阻塞队列
     * LinkedBlockingQueue  基于链表实现的阻塞队列
     * SynchronousQueue   内部没有任何容量的阻塞队列。在它内部没有任何的缓存空间
     * PriorityBlockingQueue   具有优先级的无限阻塞队列。
     */
    private final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingDeque<>();
    /**
     * 线程工厂，为线程池提供新线程的创建。ThreadFactory是一个接口，里面只有一个newThread方法。 默认为DefaultThreadFactory类。
     */
    private final ThreadFactory THREAD_FACTORY = Executors.defaultThreadFactory();
    /**
     * 拒绝策略，当任务队列已满并且线程池中的活动线程已经达到所限定的最大值或者是无法成功执行任务，这时候       * ThreadPoolExecutor会调用RejectedExecutionHandler中的rejectedExecution方法。
     * CallerRunsPolicy  只用调用者所在线程来运行任务。
     * AbortPolicy  直接抛出RejectedExecutionException异常。
     * DiscardPolicy  丢弃掉该任务，不进行处理。
     * DiscardOldestPolicy   丢弃队列里最近的一个任务，并执行当前任务。
     */
    private final RejectedExecutionHandler REJECTED_HANDLER = new ThreadPoolExecutor.AbortPolicy();
    private ThreadPoolService() {
    }
    /**
     * 单例
     * @return
     */
    public static ThreadPoolService getInstance() {
        if (sInstance == null) {
            synchronized (ThreadPoolService.class) {
                if (sInstance == null) {
                    sInstance = new ThreadPoolService();
                    sInstance.initThreadPool();
                }
            }
        }
        return sInstance;
    }
    /**
     * 初始化线程池
     */
    private void initThreadPool() {
        try {
            mThreadPoolExecutor = new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE_TIME,
                    UNIT,
                    WORK_QUEUE,
                    THREAD_FACTORY,
                    REJECTED_HANDLER);
        } catch (Exception e) {
        }
    }
    /**
     * 向线程池提交任务,无返回值
     *
     * @param runnable
     */
    public void post(Runnable runnable) {
        mThreadPoolExecutor.execute(runnable);
    }
    /**
     * 向线程池提交任务,有返回值
     *
     * @param callable
     */
    public <T> Future<T> post(Callable<T> callable) {
        RunnableFuture<T> task = new FutureTask<T>(callable);
        mThreadPoolExecutor.execute(task);
        return task;
    }
}
