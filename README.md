<h2>This is a Folder Browser/File Explorer emulator made in Java</h2>

[![](https://jitpack.io/v/alvessss/FolderBrowser.svg)](https://jitpack.io/#alvessss/FolderBrowser)
<hr>
<p>
    Heres the first look. Things like Themes, Navigation Styles, Custom-icons might come.
</p>

<img width="50%" height="50%" src="https://github.com/alvessss/FolderBrowser/blob/master/screenshot_02_version_0_1_1.png">
<hr/>

<h3> Instalation </h3>
<p>Click in the JitPack badge and follow the steps to add the library
to your project</p>

<h3>Usage</h3>

initialization:

		Context activityContext = MainActivity.this; // replace with the name of your activity
		ViewGroup fbContainerView = findViewById(R.id.fbContainerView);
		
		
		FolderBrowser fb = new FolderBrowser(
			activityContext,
			fbContainerView,
			() -> {
				// the action to perform when the user chose a file
				Log.i("File name", fb.getCurrentInode.getName());
			}
		);
		

how you launch:

		fb.setRoot(PATH_OF_ROOT_DIRECTORY);
		fb.start(pathOfStartingDirectory);


<h3>links</h3>

- Minimal Replicable Example --> https://github.com/alvessss/FolderBrowser/tree/master/app
- Main code --> https://github.com/alvessss/FolderBrowser/tree/master/FolderBrowser/src/main/java/com/alvessss/folderbrowser
- Request permission to read external directories/files --> https://developer.android.com/training/permissions/requesting