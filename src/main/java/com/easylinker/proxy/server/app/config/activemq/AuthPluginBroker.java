package com.easylinker.proxy.server.app.config.activemq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AuthPluginBroker extends BrokerFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    AuthPluginBroker(Broker next) {
        super(next);
    }

    @Override
    public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
        logger.info("来自客户端的订阅请求,Topic:" + info.getDestination().getQualifiedName());
        return super.addConsumer(context, info);
    }

    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        logger.info("addConnection:" + info.getClientId());

        super.addConnection(context, info);
    }

    @Override
    public void removeConnection(ConnectionContext context, ConnectionInfo info, Throwable error) throws Exception {

        super.removeConnection(context, info, error);
        logger.info("removeConnection:" + info.getClientId());


    }

    @Override
    public void removeSession(ConnectionContext context, SessionInfo info) throws Exception {
        super.removeSession(context, info);
        logger.info("removeSession:" + info.toString());
    }

    @Override
    public void removeConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
        super.removeConsumer(context, info);
        logger.info("removeConsumer:" + info.toString());
    }

}
