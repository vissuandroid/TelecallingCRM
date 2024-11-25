import 'dart:async';
import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:url_launcher/url_launcher.dart';


const MAX_PROGRESS = 100;

class WebViewScreen extends StatefulWidget {
  const WebViewScreen({Key? key}) : super(key: key);

  @override
  State<WebViewScreen> createState() => _WebERPState();
}

class _WebERPState extends State<WebViewScreen> {
  final Completer<InAppWebViewController> _controller =
      Completer<InAppWebViewController>();
  bool isLoading = true;
  String url = "https://everneeds.in";
  InAppWebViewController? webViewController;
  PullToRefreshController? pullToRefreshController;
  PullToRefreshSettings pullToRefreshSettings = PullToRefreshSettings();
  bool pullToRefreshEnabled = true;

  final GlobalKey webViewKey = GlobalKey();

  @override
  void initState() {
    WidgetsFlutterBinding.ensureInitialized(); // Ensure Flutter is initialized
    loadData();
    pullToRefreshController = kIsWeb
        ? null
        : PullToRefreshController(
            settings: pullToRefreshSettings,
            onRefresh: () async {
              if (defaultTargetPlatform == TargetPlatform.android) {
                webViewController?.reload();
              } else if (defaultTargetPlatform == TargetPlatform.iOS) {
                webViewController?.loadUrl(
                    urlRequest:
                        URLRequest(url: await webViewController?.getUrl()));
              }
            },
          );
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  Future<void> loadData() async {
    await Permission.camera.request();
  }

  @override
  Widget build(BuildContext context) {
    var screenheight=MediaQuery.of(context).size.height;
    return WillPopScope(
        onWillPop: () async {
          if (await webViewController!.canGoBack()) {
            webViewController!.goBack();
            return false; // Prevent default back button behavior
          }
          return true; // Allow default back button behavior
        },
        child: Scaffold(
          body: SafeArea(
            child: Stack(
              children: [
                Column(
                  children: <Widget>[
                    Expanded(
                      child: InAppWebView(
                        initialUrlRequest: URLRequest(
                          url: WebUri(url),
                        ),
                        androidOnGeolocationPermissionsShowPrompt:
                            (InAppWebViewController controller,
                                String origin) async {
                          return GeolocationPermissionShowPromptResponse(
                              origin: origin, allow: true, retain: true);
                        },
                        initialOptions: InAppWebViewGroupOptions(
                          android: AndroidInAppWebViewOptions(
                            useWideViewPort: true,
                            loadWithOverviewMode: true,
                            allowContentAccess: true,
                            geolocationEnabled: true,
                            allowFileAccess: true,
                            databaseEnabled: true,
                            domStorageEnabled: true,
                            builtInZoomControls: true,
                            displayZoomControls: false,
                            safeBrowsingEnabled: true,
                            clearSessionCache: true,
                            loadsImagesAutomatically: true,
                            thirdPartyCookiesEnabled: true,
                            blockNetworkImage: false,
                            supportMultipleWindows: true,
                          ),
                          ios: IOSInAppWebViewOptions(
                            allowsInlineMediaPlayback: true,
                          ),
                          crossPlatform: InAppWebViewOptions(
                            javaScriptEnabled: true,
                            useOnDownloadStart: true,
                            allowFileAccessFromFileURLs: true,
                            allowUniversalAccessFromFileURLs: true,
                            mediaPlaybackRequiresUserGesture: true,
                          ),
                        ),
                        androidOnPermissionRequest:
                            (InAppWebViewController controller, String origin,
                                List<String> resources) async {
                          if (resources.contains("android.permission.CAMERA")) {
                            return PermissionRequestResponse(
                              resources: resources,
                              action: PermissionRequestResponseAction.GRANT,
                            );
                          }
                          return PermissionRequestResponse(
                              resources: resources,
                              action: PermissionRequestResponseAction.DENY);
                        },
                        onWebViewCreated: (controller) {
                          webViewController = controller;
                          _controller.complete(controller);
                        },
                        pullToRefreshController: pullToRefreshController,
                        onLoadStart: (controller, url) {
                          setState(() {
                            isLoading = true; // Show loader
                          });
                        },
                        onLoadStop: (controller, url) {
                          setState(() {
                            isLoading = false; // Hide loader
                          });
                          pullToRefreshController?.endRefreshing();
                        },
                        shouldOverrideUrlLoading:
                            (controller, navigationAction) async {
                          var uri = navigationAction.request.url!;
                          if (uri.scheme == "tel" ||
                              uri.scheme == "mailto" ||
                              uri.scheme == "whatsapp") {
                            if (await canLaunch(uri.toString())) {
                              await launch(uri.toString());
                              return NavigationActionPolicy.CANCEL;
                            }
                          }
                          return NavigationActionPolicy.ALLOW;
                        },
                        onReceivedError: (controller, request, error) {
                          pullToRefreshController?.endRefreshing();
                          setState(() {
                            isLoading = false; // Hide loader on error
                          });
                        },
                        onProgressChanged: (controller, progress) {
                          if (progress == 100) {
                            setState(() {
                              isLoading =
                                  false; // Hide loader when loading is complete
                            });
                            pullToRefreshController?.endRefreshing();
                          }
                        },
                        onConsoleMessage: (controller, consoleMessage) {
                          if (kDebugMode) {
                            debugPrint("consoleMessage${consoleMessage}");
                          }
                          debugPrint(
                              "JavaScript console message: ${consoleMessage.message}");
                        },
                        onDownloadStartRequest: (controller, url) async {
                          // Handle download requests
                        },
                        initialSettings: InAppWebViewSettings(
                          allowUniversalAccessFromFileURLs: true,
                          allowFileAccessFromFileURLs: true,
                          allowFileAccess: true,
                          allowsInlineMediaPlayback: true,
                          allowsPictureInPictureMediaPlayback: true,
                          allowsBackForwardNavigationGestures: true,
                          iframeAllow: "camera;microphone;files;media;",
                          domStorageEnabled: true,
                          allowContentAccess: true,
                          javaScriptEnabled: true,
                          supportZoom: true,
                          builtInZoomControls: true,
                          displayZoomControls: false,
                          textZoom: 125,
                          blockNetworkImage: false,
                          loadsImagesAutomatically: true,
                          safeBrowsingEnabled: true,
                          useWideViewPort: true,
                          loadWithOverviewMode: true,
                          javaScriptCanOpenWindowsAutomatically: true,
                          mediaPlaybackRequiresUserGesture: false,
                          geolocationEnabled: true,
                          useOnDownloadStart: true,
                          allowsLinkPreview: true,
                          databaseEnabled: true,
                          clearSessionCache: true,
                          mediaType: "image/*",
                        ),
                      ),
                    ),
                  ],
                ),
                if (isLoading) // Show the loader when isLoading is true
                  Container(
                    height: screenheight,
                    color: Colors.white,
                    child: Center(
                      child: CircularProgressIndicator(
                        color: Colors.amberAccent,
                      ),
                    ),
                  ),
              ],
            ),
          ),
        ));
  }
}
