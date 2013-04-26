Android Utilities
=============

Library Project for utility classes that can make me(and others) more productive when I am working on next android project.

----

Getting started
=============

**Step 1**: Download the zip file of this project and unzip it.

**Step 2**: Import the project into your eclipse Android development enviornment. 

If you dont know how to import a project, follow the instructions at the link given below.
http://help.eclipse.org/helios/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftasks-importproject.htm

**Step 3**: Link this library project with your project from Properties -> Android -> Library -> Add

If you are not familiar with this, here you have to link the project imported in Step 1 with your own application.
You can follow the link below to understand how you link a particular project with this library project.
http://www.vogella.com/articles/AndroidLibraryProjects/article.html#tutorial_library_usage

And there you are good to go with the project.

----

Library methods
=============

	**Utils.java**
	public void showToast(String msg);
	public boolean isNetworkAvailable();
	public boolean isSDCARDMounted();
	public void showAlertDialog(String title, String body);
	public String toBase64(Bitmap bitmap);
	public Bitmap drawableToBitmap( Drawable drawable );
	public InputStream bitmapToInputStream(Bitmap bitmap);
	public void showProgressDialog(String title, String body, boolean isCancellable);
	public void dismissProgressDialog();
	public String readStream( InputStream in );
	public Bitmap scaleDownBitmap( Bitmap photo, int newHeight );
	public float getDensityMultiplier();

----