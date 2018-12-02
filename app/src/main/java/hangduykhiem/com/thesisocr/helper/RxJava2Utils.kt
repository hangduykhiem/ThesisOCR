package hangduykhiem.com.thesisocr.helper

import io.reactivex.CompletableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers

fun <T> applySchedulers(): ObservableTransformer<T, T> {
    return ObservableTransformer { observable ->
        observable.subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}


fun <T> applySingleSchedulers(): SingleTransformer<T, T> {
    return SingleTransformer { observable ->
        observable.subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

fun applyCompletableSchedulers(): CompletableTransformer {
    return CompletableTransformer { observable ->
        observable.subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
