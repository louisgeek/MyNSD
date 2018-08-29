package com.classichu.mynsd.nsd;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by louisgeek on 2018/8/20.
 */
public class NSDClient {
    public static final String TAG = "NSDClient";
    public static final String SERVICE_TYPE = "_http._tcp.";
    private Context mContext;
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;

    private Map<String, NsdServiceInfo> mNsdServiceInfoMap = new HashMap<>();

    public NSDClient(Context context) {
        mContext = context;
    }

    public void init() {
        mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
        //初始化监听：发现服务
        initDiscoveryListener();
    }

    private void initDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onShowLog("服务发现启动，regType：" + regType);
                }
            }

            @Override
            public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success" + nsdServiceInfo);
              /*  if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + mServiceName);
                } else if (service.getServiceName().contains(mServiceName)) {
                    mNsdManager.resolveService(service, mResolveListener);
                }*/
                //
                if (!nsdServiceInfo.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + nsdServiceInfo.getServiceType());
                } else {
                    if (mOnNsdServiceInfoStateListener != null) {
                        mOnNsdServiceInfoStateListener.onShowLog("服务发现成功，nsdServiceInfo：" + nsdServiceInfo);
                    }
                    resolveService(nsdServiceInfo);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + nsdServiceInfo);
               /* if (mNsdServiceInfo == service) {
                    mNsdServiceInfo = null;
                }*/
                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onShowLog("服务消失，nsdServiceInfo：" + nsdServiceInfo);
                }
                mNsdServiceInfoMap.remove(nsdServiceInfo.getServiceName());
                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onStateChange(mNsdServiceInfoMap);
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onShowLog("服务发现结束，serviceType:" + serviceType);
                }
                mNsdServiceInfoMap.clear();
                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onStateChange(mNsdServiceInfoMap);
                }
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onShowLog("服务发现启动失败:" + errorCode + "，serviceType:" + serviceType);
                }
//                mNsdManager.stopServiceDiscovery(this);
                stopServiceDiscovery();

            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onShowLog("服务发现停止失败:" + errorCode + "，serviceType:" + serviceType);
                }
//                mNsdManager.stopServiceDiscovery(this);
                stopServiceDiscovery();
            }
        };
    }

    public void discoverServices() {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    private void resolveService(NsdServiceInfo nsdServiceInfo) {
        mNsdManager.resolveService(nsdServiceInfo, new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve failed: " + errorCode);
                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onShowLog("服务解析失败:" + errorCode + "，serviceInfo:" + serviceInfo);
                }
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onShowLog("服务解析成功，serviceInfo:" + serviceInfo);
                }
                mNsdServiceInfoMap.put(serviceInfo.getServiceName(), serviceInfo);
                if (mOnNsdServiceInfoStateListener != null) {
                    mOnNsdServiceInfoStateListener.onStateChange(mNsdServiceInfoMap);
                }
            }
        });
    }

    public void stopServiceDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    ///
    private OnNsdServiceInfoStateListener mOnNsdServiceInfoStateListener;

    public void setOnNsdServiceInfoStateListener(OnNsdServiceInfoStateListener onNsdServiceInfoStateListener) {
        mOnNsdServiceInfoStateListener = onNsdServiceInfoStateListener;
    }

    public interface OnNsdServiceInfoStateListener {
        void onStateChange(Map<String, NsdServiceInfo> nsdServiceInfoMap);

        void onShowLog(String log);
    }

}
