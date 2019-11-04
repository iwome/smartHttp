package com.bbq.smartokhttp.secure;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by bangbang.qiu on 2019/10/30.
 */
public class SSLHelper {
    public SSLSocketFactory sSLSocketFactory;
    public X509TrustManager trustManager;

    public SSLHelper() {
    }

    public static SSLHelper getSslSocketFactory(InputStream[] certificates, InputStream[] bksFile, String password) {
        SSLHelper sslParams = new SSLHelper();

        try {
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager = null;
            if (trustManagers != null) {
                trustManager = chooseTrustManager(trustManagers);
            }

            if (trustManager == null) {
                return sslParams;
            } else {
                sslContext.init(null, new TrustManager[]{trustManager}, null);
                sslParams.sSLSocketFactory = sslContext.getSocketFactory();
                sslParams.trustManager = trustManager;
                return sslParams;
            }
        } catch (NoSuchAlgorithmException var7) {
            throw new AssertionError(var7);
        } catch (KeyManagementException var8) {
            throw new AssertionError(var8);
        } catch (Exception var9) {
            throw new AssertionError(var9);
        }
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates != null && certificates.length > 0) {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null);
                int index = 0;
                int var5 = certificates.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    InputStream certificateStream = certificates[var6];
                    String certificateAlias = Integer.toString(index++);
                    Certificate certificate = certificateFactory.generateCertificate(certificateStream);

                    try {
                        ((X509Certificate)certificate).checkValidity();
                        keyStore.setCertificateEntry(certificateAlias, certificate);
                    } catch (Exception var23) {
                        var23.printStackTrace();
                    } finally {
                        try {
                            if (certificateStream != null) {
                                certificateStream.close();
                            }
                        } catch (IOException var22) {
                        }

                    }
                }

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                return trustManagers;
            } catch (NoSuchAlgorithmException var25) {
                var25.printStackTrace();
            } catch (CertificateException var26) {
                var26.printStackTrace();
            } catch (KeyStoreException var27) {
                var27.printStackTrace();
            } catch (Exception var28) {
                var28.printStackTrace();
            }

            return null;
        } else {
            return null;
        }
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile != null && password != null) {
                KeyStore clientKeyStore = KeyStore.getInstance("BKS");
                clientKeyStore.load(bksFile, password.toCharArray());
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(clientKeyStore, password.toCharArray());
                return keyManagerFactory.getKeyManagers();
            }

            return null;
        } catch (KeyStoreException var4) {
            var4.printStackTrace();
        } catch (NoSuchAlgorithmException var5) {
            var5.printStackTrace();
        } catch (UnrecoverableKeyException var6) {
            var6.printStackTrace();
        } catch (CertificateException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        TrustManager[] var1 = trustManagers;
        int var2 = trustManagers.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            TrustManager trustManager = var1[var3];
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager)trustManager;
            }
        }

        return null;
    }

    public static Certificate getValidCertificate(InputStream... certificates) {
        if (certificates == null) {
            return null;
        } else {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                InputStream[] var2 = certificates;
                int var3 = certificates.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    InputStream certificateStream = var2[var4];
                    Certificate certificate = certificateFactory.generateCertificate(certificateStream);

                    try {
                        ((X509Certificate)certificate).checkValidity();
                        Certificate var7 = certificate;
                        return var7;
                    } catch (Exception var18) {
                        var18.printStackTrace();
                    } finally {
                        try {
                            if (certificateStream != null) {
                                certificateStream.close();
                            }
                        } catch (IOException var17) {
                        }

                    }
                }

                return null;
            } catch (Exception var20) {
                var20.printStackTrace();
                return null;
            }
        }
    }
}
