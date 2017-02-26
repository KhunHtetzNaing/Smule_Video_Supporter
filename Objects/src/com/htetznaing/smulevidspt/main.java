package com.htetznaing.smulevidspt;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.smulevidspt", "com.htetznaing.smulevidspt.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.htetznaing.smulevidspt", "com.htetznaing.smulevidspt.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.smulevidspt.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _ad1 = null;
public static anywheresoftware.b4a.objects.Timer _ad2 = null;
public static anywheresoftware.b4a.objects.Timer _t = null;
public MLfiles.Fileslib.MLfiles _ml = null;
public static String _st = "";
public static String _s = "";
public anywheresoftware.b4a.phone.Phone _p = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b2 = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _bbg = null;
public de.amberhome.objects.floatingactionbutton.FloatingActionButtonWrapper _fb = null;
public de.amberhome.objects.floatingactionbutton.FloatingActionButtonWrapper _fb1 = null;
public anywheresoftware.b4a.object.XmlLayoutBuilder _res = null;
public b4a.util.BClipboard _copy = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb = null;
public anywheresoftware.b4a.phone.Phone _ph = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _banner = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper _interstitial = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbf = null;
public com.htetznaing.smulevidspt.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
int _height = 0;
 //BA.debugLineNum = 42;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 43;BA.debugLine="Banner.Initialize2(\"Banner\",\"ca-app-pub-41733485";
mostCurrent._banner.Initialize2(mostCurrent.activityBA,"Banner","ca-app-pub-4173348573252986/1500796559",mostCurrent._banner.SIZE_SMART_BANNER);
 //BA.debugLineNum = 44;BA.debugLine="Dim height As Int";
_height = 0;
 //BA.debugLineNum = 45;BA.debugLine="If GetDeviceLayoutValues.ApproximateScreenSize <";
if (anywheresoftware.b4a.keywords.Common.GetDeviceLayoutValues(mostCurrent.activityBA).getApproximateScreenSize()<6) { 
 //BA.debugLineNum = 46;BA.debugLine="If 100%x > 100%y Then height = 32dip Else height";
if (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)>anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)) { 
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32));}
else {
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50));};
 }else {
 //BA.debugLineNum = 48;BA.debugLine="height = 90dip";
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (90));
 };
 //BA.debugLineNum = 50;BA.debugLine="Activity.AddView(Banner,0dip,100%y - height,100";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._banner.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-_height),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_height);
 //BA.debugLineNum = 51;BA.debugLine="Banner.LoadAd";
mostCurrent._banner.LoadAd();
 //BA.debugLineNum = 53;BA.debugLine="Interstitial.Initialize(\"Interstitial\",\"ca-app-pu";
mostCurrent._interstitial.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/4454262959");
 //BA.debugLineNum = 54;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 56;BA.debugLine="ad1.Initialize(\"ad1\",100)";
_ad1.Initialize(processBA,"ad1",(long) (100));
 //BA.debugLineNum = 57;BA.debugLine="ad1.Enabled = False";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 58;BA.debugLine="ad2.Initialize(\"ad2\",15000)";
_ad2.Initialize(processBA,"ad2",(long) (15000));
 //BA.debugLineNum = 59;BA.debugLine="ad2.Enabled = True";
_ad2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 61;BA.debugLine="ph.SetScreenOrientation(1)";
mostCurrent._ph.SetScreenOrientation(processBA,(int) (1));
 //BA.debugLineNum = 62;BA.debugLine="ml.GetRoot";
mostCurrent._ml.GetRoot();
 //BA.debugLineNum = 63;BA.debugLine="Activity.LoadLayout(\"Lay1\")";
mostCurrent._activity.LoadLayout("Lay1",mostCurrent.activityBA);
 //BA.debugLineNum = 64;BA.debugLine="fb.Icon = res.GetDrawable(\"ic_add_white_24dp\")";
mostCurrent._fb.setIcon(mostCurrent._res.GetDrawable("ic_add_white_24dp"));
 //BA.debugLineNum = 65;BA.debugLine="fb.HideOffset = 100%y - fb.Top";
mostCurrent._fb.setHideOffset((int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-mostCurrent._fb.getTop()));
 //BA.debugLineNum = 66;BA.debugLine="fb.Hide(False)";
mostCurrent._fb.Hide(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 67;BA.debugLine="fb.Show(True)";
mostCurrent._fb.Show(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 69;BA.debugLine="fb1.Icon = res.GetDrawable(\"about\")";
mostCurrent._fb1.setIcon(mostCurrent._res.GetDrawable("about"));
 //BA.debugLineNum = 70;BA.debugLine="fb1.HideOffset = 100%y - fb.Top";
mostCurrent._fb1.setHideOffset((int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-mostCurrent._fb.getTop()));
 //BA.debugLineNum = 71;BA.debugLine="fb1.Hide(False)";
mostCurrent._fb1.Hide(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 72;BA.debugLine="fb1.Show(True)";
mostCurrent._fb1.Show(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 74;BA.debugLine="lb.Initialize(\"\")";
mostCurrent._lb.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 75;BA.debugLine="lb.Text = \"This App Requires #Root Access\" & CRLF";
mostCurrent._lb.setText((Object)("This App Requires #Root Access"+anywheresoftware.b4a.keywords.Common.CRLF+"Support Sing! SMULE Video Recording Feature"+anywheresoftware.b4a.keywords.Common.CRLF+"On All Android Devices!"));
 //BA.debugLineNum = 76;BA.debugLine="lb.Gravity = Gravity.CENTER";
mostCurrent._lb.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 77;BA.debugLine="Activity.AddView(lb,0%x,10%y,100%x,100dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lb.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 79;BA.debugLine="bbg.Initialize(Colors.RGB(0,188,196),15)";
mostCurrent._bbg.Initialize(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (188),(int) (196)),(int) (15));
 //BA.debugLineNum = 80;BA.debugLine="b1.Initialize(\"b1\")";
mostCurrent._b1.Initialize(mostCurrent.activityBA,"b1");
 //BA.debugLineNum = 81;BA.debugLine="b1.Text = \"Unlock Video Fearure!\"";
mostCurrent._b1.setText((Object)("Unlock Video Fearure!"));
 //BA.debugLineNum = 82;BA.debugLine="b1.Background = bbg";
mostCurrent._b1.setBackground((android.graphics.drawable.Drawable)(mostCurrent._bbg.getObject()));
 //BA.debugLineNum = 84;BA.debugLine="b2.Initialize(\"b2\")";
mostCurrent._b2.Initialize(mostCurrent.activityBA,"b2");
 //BA.debugLineNum = 85;BA.debugLine="b2.Text = \"Restore Default\"";
mostCurrent._b2.setText((Object)("Restore Default"));
 //BA.debugLineNum = 86;BA.debugLine="b2.Background = bbg";
mostCurrent._b2.setBackground((android.graphics.drawable.Drawable)(mostCurrent._bbg.getObject()));
 //BA.debugLineNum = 88;BA.debugLine="Activity.AddView(b1,20%x,(lb.Top+lb.Height)+5%y,6";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) ((mostCurrent._lb.getTop()+mostCurrent._lb.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 89;BA.debugLine="Activity.AddView(b2,20%x,(b1.Top+b1.Height)+3%y,6";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b2.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) ((mostCurrent._b1.getTop()+mostCurrent._b1.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (3),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 91;BA.debugLine="lbf.Initialize(\"lbf\")";
mostCurrent._lbf.Initialize(mostCurrent.activityBA,"lbf");
 //BA.debugLineNum = 92;BA.debugLine="lbf.Text = \"Powered By Myanmar Android Apps\"";
mostCurrent._lbf.setText((Object)("Powered By Myanmar Android Apps"));
 //BA.debugLineNum = 93;BA.debugLine="Activity.AddView(lbf,0%x,(b2.Top+b2.Height)+10%y,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lbf.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) ((mostCurrent._b2.getTop()+mostCurrent._b2.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 94;BA.debugLine="lbf.Gravity = Gravity.CENTER";
mostCurrent._lbf.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 95;BA.debugLine="lbf.TextColor = Colors.White";
mostCurrent._lbf.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 97;BA.debugLine="t.Initialize(\"t\",3000)";
_t.Initialize(processBA,"t",(long) (3000));
 //BA.debugLineNum = 98;BA.debugLine="t.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
int _answ = 0;
anywheresoftware.b4a.objects.IntentWrapper _facebook = null;
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 211;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 212;BA.debugLine="Dim Answ As Int";
_answ = 0;
 //BA.debugLineNum = 213;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 214;BA.debugLine="Answ = Msgbox2(\"If you want to get new updates o";
_answ = anywheresoftware.b4a.keywords.Common.Msgbox2("If you want to get new updates on  Facebook? Please Like "+anywheresoftware.b4a.keywords.Common.CRLF+"Myanmar Android Apps Page!","Attention!","Yes","","No",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fb.png").getObject()),mostCurrent.activityBA);
 //BA.debugLineNum = 215;BA.debugLine="If Answ = DialogResponse.NEGATIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 216;BA.debugLine="ad1.Enabled = True";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 217;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 };
 //BA.debugLineNum = 220;BA.debugLine="If Answ = DialogResponse.POSITIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 221;BA.debugLine="ad1.Enabled = True";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 222;BA.debugLine="Try";
try { //BA.debugLineNum = 224;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 226;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"fb:/";
_facebook.Initialize(_facebook.ACTION_VIEW,"fb://page/627699334104477");
 //BA.debugLineNum = 227;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_facebook.getObject()));
 } 
       catch (Exception e16) {
			processBA.setLastException(e16); //BA.debugLineNum = 231;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 232;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"https://m.facebook";
_i.Initialize(_i.ACTION_VIEW,"https://m.facebook.com/MmFreeAndroidApps");
 //BA.debugLineNum = 234;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 };
 //BA.debugLineNum = 237;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 239;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 198;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 200;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 194;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 196;BA.debugLine="End Sub";
return "";
}
public static String  _ad1_tick() throws Exception{
 //BA.debugLineNum = 202;BA.debugLine="Sub ad1_Tick";
 //BA.debugLineNum = 203;BA.debugLine="If Interstitial.Ready Then Interstitial.Show Else";
if (mostCurrent._interstitial.getReady()) { 
mostCurrent._interstitial.Show();}
else {
mostCurrent._interstitial.LoadAd();};
 //BA.debugLineNum = 204;BA.debugLine="ad1.Enabled = False";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 205;BA.debugLine="End Sub";
return "";
}
public static String  _ad2_tick() throws Exception{
 //BA.debugLineNum = 207;BA.debugLine="Sub ad2_Tick";
 //BA.debugLineNum = 208;BA.debugLine="If Interstitial.Ready Then Interstitial.Show Else";
if (mostCurrent._interstitial.getReady()) { 
mostCurrent._interstitial.Show();}
else {
mostCurrent._interstitial.LoadAd();};
 //BA.debugLineNum = 209;BA.debugLine="End Sub";
return "";
}
public static String  _b1_click() throws Exception{
 //BA.debugLineNum = 120;BA.debugLine="Sub b1_Click";
 //BA.debugLineNum = 121;BA.debugLine="ad1.Enabled = True";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 122;BA.debugLine="ml.GetRoot";
mostCurrent._ml.GetRoot();
 //BA.debugLineNum = 123;BA.debugLine="If ml.HaveRoot Then";
if (mostCurrent._ml.HaveRoot) { 
 //BA.debugLineNum = 124;BA.debugLine="ml.RootCmd(\"mount -o rw,remount /system\",\"\",Null";
mostCurrent._ml.RootCmd("mount -o rw,remount /system","",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 125;BA.debugLine="File.Copy(\"system\",\"build.prop\",File.DirRootExte";
anywheresoftware.b4a.keywords.Common.File.Copy("system","build.prop",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"build.prop");
 //BA.debugLineNum = 126;BA.debugLine="st = File.ReadString(File.DirRootExternal,\"build";
mostCurrent._st = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"build.prop");
 //BA.debugLineNum = 127;BA.debugLine="s = st.Replace(\"ro.product.model=\"& p.Model,\"ro.";
mostCurrent._s = mostCurrent._st.replace("ro.product.model="+mostCurrent._p.getModel(),"ro.product.model=GT-I9505");
 //BA.debugLineNum = 128;BA.debugLine="File.WriteString(File.DirRootExternal,\"build.pro";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"build.prop",mostCurrent._s);
 //BA.debugLineNum = 130;BA.debugLine="If File.Exists(File.DirRootExternal & \"/Android\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Android","Ht3tzN4ing.txt")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 131;BA.debugLine="File.WriteString(File.DirRootExternal & \"/Andro";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Android","Ht3tzN4ing.txt","My Name is Khun Htetz Naing"+anywheresoftware.b4a.keywords.Common.CRLF+"www.htetznaing.com");
 //BA.debugLineNum = 132;BA.debugLine="ml.mv(\"/system/build.prop\",\"/system/build.prop.";
mostCurrent._ml.mv("/system/build.prop","/system/build.prop.Ht3tzN4ing");
 };
 //BA.debugLineNum = 135;BA.debugLine="ml.RootCmd(\"cp -r \"&File.DirRootExternal&\"/build";
mostCurrent._ml.RootCmd("cp -r "+anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/build.prop system/build.prop","",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 136;BA.debugLine="ml.chmod(\"/system/build.prop\",644)";
mostCurrent._ml.chmod("/system/build.prop",BA.NumberToString(644));
 //BA.debugLineNum = 137;BA.debugLine="Msgbox(\"Congratutions! Now Your Device Supported";
anywheresoftware.b4a.keywords.Common.Msgbox("Congratutions! Now Your Device Supported Video in Smule!"+anywheresoftware.b4a.keywords.Common.CRLF+"Now Your device will be Reboot...","Completed",mostCurrent.activityBA);
 //BA.debugLineNum = 138;BA.debugLine="ml.RootCmd(\"reboot\",\"\",Null,Null,False)";
mostCurrent._ml.RootCmd("reboot","",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 140;BA.debugLine="Msgbox(\"Sorry! Your Device is Not Have Root Acce";
anywheresoftware.b4a.keywords.Common.Msgbox("Sorry! Your Device is Not Have Root Access. Please Rooting First!","Error",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 142;BA.debugLine="End Sub";
return "";
}
public static String  _b2_click() throws Exception{
 //BA.debugLineNum = 144;BA.debugLine="Sub b2_Click";
 //BA.debugLineNum = 145;BA.debugLine="ad1.Enabled = True";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 146;BA.debugLine="ml.GetRoot";
mostCurrent._ml.GetRoot();
 //BA.debugLineNum = 147;BA.debugLine="If ml.HaveRoot Then";
if (mostCurrent._ml.HaveRoot) { 
 //BA.debugLineNum = 148;BA.debugLine="ml.RootCmd(\"mount -o rw,remount /system\",\"\",Null";
mostCurrent._ml.RootCmd("mount -o rw,remount /system","",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 149;BA.debugLine="If File.Exists(File.DirRootExternal & \"/Android\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Android","Ht3tzN4ing.txt")==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 150;BA.debugLine="File.Delete(File.DirRootExternal & \"/Android\",\"";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Android","Ht3tzN4ing.txt");
 //BA.debugLineNum = 151;BA.debugLine="ml.rm(\"/system/build.prop\")";
mostCurrent._ml.rm("/system/build.prop");
 //BA.debugLineNum = 152;BA.debugLine="ml.mv(\"/system/build.prop.Ht3tzN4ing\",\"/system/";
mostCurrent._ml.mv("/system/build.prop.Ht3tzN4ing","/system/build.prop");
 //BA.debugLineNum = 153;BA.debugLine="Msgbox(\"Original Restored \" & CRLF & \"Now Your";
anywheresoftware.b4a.keywords.Common.Msgbox("Original Restored "+anywheresoftware.b4a.keywords.Common.CRLF+"Now Your device will be Reboot...","Completed",mostCurrent.activityBA);
 //BA.debugLineNum = 154;BA.debugLine="ml.RootCmd(\"reboot\",\"\",Null,Null,False)";
mostCurrent._ml.RootCmd("reboot","",(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(anywheresoftware.b4a.keywords.Common.Null),anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 156;BA.debugLine="Msgbox(\"Unlock Video Supoort First!\",\"Error\")";
anywheresoftware.b4a.keywords.Common.Msgbox("Unlock Video Supoort First!","Error",mostCurrent.activityBA);
 };
 }else {
 //BA.debugLineNum = 159;BA.debugLine="Msgbox(\"Sorry! Your Device is Not Have Root Acce";
anywheresoftware.b4a.keywords.Common.Msgbox("Sorry! Your Device is Not Have Root Access. Please Rooting First!","Error",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
return "";
}
public static String  _fb_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _facebook = null;
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 163;BA.debugLine="Sub fb_Click";
 //BA.debugLineNum = 164;BA.debugLine="ToastMessageShow(\"App Update နွင့္ App အသစ္ေတြ \"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("App Update နွင့္ App အသစ္ေတြ "+anywheresoftware.b4a.keywords.Common.CRLF+"Facebook ကေနဖတ္ရူ့သိနိုင္ဖို့"+anywheresoftware.b4a.keywords.Common.CRLF+"Myanmar Android Apps ကို Like ထားလိုက္ပါ။",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 165;BA.debugLine="Try";
try { //BA.debugLineNum = 167;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 169;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"fb://";
_facebook.Initialize(_facebook.ACTION_VIEW,"fb://page/627699334104477");
 //BA.debugLineNum = 170;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_facebook.getObject()));
 } 
       catch (Exception e7) {
			processBA.setLastException(e7); //BA.debugLineNum = 174;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 175;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"https://m.facebook.";
_i.Initialize(_i.ACTION_VIEW,"https://m.facebook.com/MmFreeAndroidApps");
 //BA.debugLineNum = 177;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 };
 //BA.debugLineNum = 180;BA.debugLine="End Sub";
return "";
}
public static String  _fb1_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _shareit = null;
 //BA.debugLineNum = 182;BA.debugLine="Sub fb1_Click";
 //BA.debugLineNum = 183;BA.debugLine="Dim ShareIt As Intent";
_shareit = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 184;BA.debugLine="copy.clrText";
mostCurrent._copy.clrText(mostCurrent.activityBA);
 //BA.debugLineNum = 185;BA.debugLine="copy.setText(\"#SmuleVideoSupporter Android App Fo";
mostCurrent._copy.setText(mostCurrent.activityBA,"#SmuleVideoSupporter Android App For Unlock Video Recording Feature in Sing!  Karaoke by SMULE! On All Android Devices!"+anywheresoftware.b4a.keywords.Common.CRLF+"Download Free at : www.htetznaing.com/Smule"+anywheresoftware.b4a.keywords.Common.CRLF+" #Ht3tzN4ing"+anywheresoftware.b4a.keywords.Common.CRLF+" #MyanmarAndroidApps");
 //BA.debugLineNum = 186;BA.debugLine="ShareIt.Initialize (ShareIt.ACTION_SEND,\"\")";
_shareit.Initialize(_shareit.ACTION_SEND,"");
 //BA.debugLineNum = 187;BA.debugLine="ShareIt.SetType (\"text/plain\")";
_shareit.SetType("text/plain");
 //BA.debugLineNum = 188;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.TEXT\",cop";
_shareit.PutExtra("android.intent.extra.TEXT",(Object)(mostCurrent._copy.getText(mostCurrent.activityBA)));
 //BA.debugLineNum = 189;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.SUBJECT\",";
_shareit.PutExtra("android.intent.extra.SUBJECT",(Object)("Get Free!!"));
 //BA.debugLineNum = 190;BA.debugLine="ShareIt.WrapAsIntentChooser(\"Share App Via...\")";
_shareit.WrapAsIntentChooser("Share App Via...");
 //BA.debugLineNum = 191;BA.debugLine="StartActivity (ShareIt)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_shareit.getObject()));
 //BA.debugLineNum = 193;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 24;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 27;BA.debugLine="Dim ml As MLfiles";
mostCurrent._ml = new MLfiles.Fileslib.MLfiles();
 //BA.debugLineNum = 28;BA.debugLine="Dim st,s As String";
mostCurrent._st = "";
mostCurrent._s = "";
 //BA.debugLineNum = 29;BA.debugLine="Dim p As Phone";
mostCurrent._p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 30;BA.debugLine="Dim b1,b2 As Button";
mostCurrent._b1 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._b2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim bbg As ColorDrawable";
mostCurrent._bbg = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 32;BA.debugLine="Dim fb,fb1 As FloatingActionButton";
mostCurrent._fb = new de.amberhome.objects.floatingactionbutton.FloatingActionButtonWrapper();
mostCurrent._fb1 = new de.amberhome.objects.floatingactionbutton.FloatingActionButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim res As XmlLayoutBuilder";
mostCurrent._res = new anywheresoftware.b4a.object.XmlLayoutBuilder();
 //BA.debugLineNum = 34;BA.debugLine="Dim copy As BClipboard";
mostCurrent._copy = new b4a.util.BClipboard();
 //BA.debugLineNum = 35;BA.debugLine="Dim lb As Label";
mostCurrent._lb = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Dim ph As Phone";
mostCurrent._ph = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 37;BA.debugLine="Dim Banner As AdView";
mostCurrent._banner = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Dim Interstitial As InterstitialAd";
mostCurrent._interstitial = new anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Dim lbf As Label";
mostCurrent._lbf = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="End Sub";
return "";
}
public static String  _lbf_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _facebook = null;
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 101;BA.debugLine="Sub lbf_Click";
 //BA.debugLineNum = 102;BA.debugLine="ToastMessageShow(\"App Update နွင့္ App အသစ္ေတြ \"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("App Update နွင့္ App အသစ္ေတြ "+anywheresoftware.b4a.keywords.Common.CRLF+"Facebook ကေနဖတ္ရူ့သိနိုင္ဖို့"+anywheresoftware.b4a.keywords.Common.CRLF+"Myanmar Android Apps ကို Like ထားလိုက္ပါ။",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 103;BA.debugLine="Try";
try { //BA.debugLineNum = 105;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 107;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"fb://";
_facebook.Initialize(_facebook.ACTION_VIEW,"fb://page/627699334104477");
 //BA.debugLineNum = 108;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_facebook.getObject()));
 } 
       catch (Exception e7) {
			processBA.setLastException(e7); //BA.debugLineNum = 112;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 113;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"https://m.facebook.";
_i.Initialize(_i.ACTION_VIEW,"https://m.facebook.com/MmFreeAndroidApps");
 //BA.debugLineNum = 115;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 };
 //BA.debugLineNum = 118;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 17;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 20;BA.debugLine="Dim ad1,ad2 As Timer";
_ad1 = new anywheresoftware.b4a.objects.Timer();
_ad2 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 21;BA.debugLine="Dim t As Timer";
_t = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return "";
}
}
