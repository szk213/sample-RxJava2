package com.github.szk213.sample.rxjava2;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class Sample {

	public static void main(String[] args) throws InterruptedException {
		FlowableOnSubscribe<String> flowableOnSubscibe = new FlowableOnSubscribe<String>() {
			@Override
			public void subscribe(FlowableEmitter<String> emitter) throws Exception {
				String[] datas = { "Hello, World!", "こんにちは、世界！" };

				for (String data : datas) {
					// 購読解除されている場合は処理をやめる
					if (emitter.isCancelled()) {
						return;
					}


//					if(data.equals("こんにちは、世界！")) {
//						throw new Exception("error");
//					}

					// データを通知する
					emitter.onNext(data);
				}
			}
		};
		Subscriber<String> subscriber = new Subscriber<String>() {

			/** データ数のリクエストおよび購読の解除を行うオブジェクト */
			private Subscription subscription;

			// 購読が開始された際の処理
			@Override
			public void onSubscribe(Subscription subscription) {
				// SubscriptionをSubscriber内で保持する
				this.subscription = subscription;
				// 受け取るデータ数をリクエストする
				this.subscription.request(1L);
			}

			// データを受け取った際の処理
			@Override
			public void onNext(String item) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 実行しているスレッド名の取得
				String threadName = Thread.currentThread().getName();
				// 受け取ったデータを出力する
				System.out.println(threadName + ": " + item);

				// 次に受け取るデータ数をリクエストする
				this.subscription.request(1L);
			}

			// 完了を通知された際の処理
			@Override
			public void onComplete() {
				// 実行しているスレッド名の取得
				String threadName = Thread.currentThread().getName();
				System.out.println(threadName + ": 完了しました");
			}

			// エラーを通知された際の処理
			@Override
			public void onError(Throwable error) {
				error.printStackTrace();
			}
		};
		Flowable<String> flowable = Flowable.create(flowableOnSubscibe, BackpressureStrategy.BUFFER);

		flowable.observeOn(Schedulers.computation()).subscribe(subscriber);

		Thread.sleep(500);
	}

}
