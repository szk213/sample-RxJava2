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
				String[] datas = { "Hello, World!", "����ɂ��́A���E�I" };

				for (String data : datas) {
					// �w�ǉ�������Ă���ꍇ�͏�������߂�
					if (emitter.isCancelled()) {
						return;
					}


//					if(data.equals("����ɂ��́A���E�I")) {
//						throw new Exception("error");
//					}

					// �f�[�^��ʒm����
					emitter.onNext(data);
				}
			}
		};
		Subscriber<String> subscriber = new Subscriber<String>() {

			/** �f�[�^���̃��N�G�X�g����эw�ǂ̉������s���I�u�W�F�N�g */
			private Subscription subscription;

			// �w�ǂ��J�n���ꂽ�ۂ̏���
			@Override
			public void onSubscribe(Subscription subscription) {
				// Subscription��Subscriber���ŕێ�����
				this.subscription = subscription;
				// �󂯎��f�[�^�������N�G�X�g����
				this.subscription.request(1L);
			}

			// �f�[�^���󂯎�����ۂ̏���
			@Override
			public void onNext(String item) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// ���s���Ă���X���b�h���̎擾
				String threadName = Thread.currentThread().getName();
				// �󂯎�����f�[�^���o�͂���
				System.out.println(threadName + ": " + item);

				// ���Ɏ󂯎��f�[�^�������N�G�X�g����
				this.subscription.request(1L);
			}

			// ������ʒm���ꂽ�ۂ̏���
			@Override
			public void onComplete() {
				// ���s���Ă���X���b�h���̎擾
				String threadName = Thread.currentThread().getName();
				System.out.println(threadName + ": �������܂���");
			}

			// �G���[��ʒm���ꂽ�ۂ̏���
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
