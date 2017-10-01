package com.github.frapontillo.pulse.crowd.postagsimple;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.data.entity.Token;
import com.github.frapontillo.pulse.rx.PulseSubscriber;
import com.github.frapontillo.pulse.spi.IPlugin;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public abstract class ISimplePOSTaggerOperator implements Observable.Operator<Message, Message> {
    private IPlugin plugin;

    public ISimplePOSTaggerOperator(IPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new PulseSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                plugin.reportElementAsStarted(message.getId());
                message = posTagMessage(message);
                plugin.reportElementAsEnded(message.getId());
                subscriber.onNext(message);
            }

            @Override public void onCompleted() {
                plugin.reportPluginAsCompleted();
                super.onCompleted();
            }

            @Override public void onError(Throwable e) {
                plugin.reportPluginAsErrored();
                super.onError(e);
            }
        };
    }

    /**
     * Tags all the {@link Token}s of the {@link Message}, then return the input {@link Message}.
     *
     * @param message The {@link Message} to process.
     *
     * @return the {@link Message} with POS-tagged {@link Token}s.
     */
    public Message posTagMessage(Message message) {
        posTagMessageTokens(message);
        return message;
    }

    /**
     * Tags the given {@link Token}s with Part-Of-Speech named entities.
     *
     * @param message The {@link Message} whose {@link List} of {@link Token}s will be modified in
     *                order to include the related Part-Of-Speech tags.
     *
     * @return The same {@link List} of {@link Token}s of the input {@link Message}, eventually
     * modified.
     */
    public abstract List<Token> posTagMessageTokens(Message message);

}
