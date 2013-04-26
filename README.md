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

	/***
	 * Shows the message passed in the parameter in the Toast.
	 * 
	 * @param msg Message to be show in the toast. 
	 * ***/
	public void showToast(String msg);
	
	/***
	 * Checks if the Internet connection is available.
	 * @return Returns true if the Internet connection is available. False otherwise.
	 * **/
	public boolean isNetworkAvailable();
	
	/***
	 * Checks if the SD Card is mounted on the device.
	 * ***/
	public boolean isSDCARDMounted();
	
	/***
	 * Show an alert dialog with the OK button.
	 * When the user presses OK button, the dialog dismisses.
	 * ***/
	public void showAlertDialog(String title, String body);
	
	/***
	 * Serializes the Bitmap to Base64
	 * ***/
	public String toBase64(Bitmap bitmap);
	
	
	/***
	 * Converts the passed in drawable to Bitmap
	 * representation
	 * ***/
	public Bitmap drawableToBitmap( Drawable drawable );
	
	/***
	 * Converts the given bitmap to {@linkplain InputStream}.
	 * @throws NullPointerException If the parameter bitmap is null.
	 * ***/
	public InputStream bitmapToInputStream(Bitmap bitmap);
	
	/***
	 * Show a progress dialog with a spinning animation in it.
	 * 
	 * @param title Title of the progress dialog
	 * @param body Body/Message to be shown in the progress dialog
	 * @param isCancellable True if the dialog can be cancelled on back button press, false otherwise
	 ***/
	public void showProgressDialog(String title, String body, boolean isCancellable);

	/***
	 * Dismiss the progress dialog if it is visible.
	 * **/
	public void dismissProgressDialog();
	
	/***
	 * Read the {@link InputStream} and convert the data received
	 * into the {@link String}
	 * ***/
	public String readStream( InputStream in );
	
	/***
	 * Scales the image depending upon the display density of the
	 * device.
	 * 
	 * When dealing with the bitmaps of bigger size, this method must be called
	 * from a non-UI thread.
	 * ***/
	public Bitmap scaleDownBitmap( Bitmap photo, int newHeight );
	
	/***
	 * Gives the device independent constant which can be used for scaling images,
	 * manipulating view sizes and changing dimension etc.
	 * ***/
	public float getDensityMultiplier();

----