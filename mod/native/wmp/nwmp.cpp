#include <jni.h>
#include <stdio.h>
#include <windows.h>
#include <wmp.h>
#include "nwmp.h"

#include "atlbase.h"
#include "atlwin.h"

JNIEXPORT void JNICALL Java_eu_the5zig_mod_manager_WindowsMediaPlayerManager_test(JNIEnv *, jobject)
{
	CoInitialize(NULL);

	HRESULT hr = S_OK;
	IWMPMedia* current_song;
	CComBSTR bstrVersionInfo; // Contains the version string.
	CComPtr<IWMPPlayer> spPlayer;  // Smart pointer to IWMPPlayer interface.

	hr = spPlayer.CoCreateInstance(__uuidof(WindowsMediaPlayer), 0, CLSCTX_INPROC_SERVER);

	if (SUCCEEDED(hr))
	{
		hr = spPlayer->get_versionInfo(&bstrVersionInfo);
	}

	COLE2T pStr(bstrVersionInfo);
	MessageBox(NULL, (LPCSTR)pStr, _T("Windows Media Player"), MB_OK);

	double duration;
	if (SUCCEEDED(hr))
	{
		// Show the version in a message box.
		hr = current_song->get_duration(&duration);
		//hr = spPlayer->get_versionInfo(&bstrVersionInfo);

		MessageBox(NULL, _T("2"), _T("Windows Media Player"), MB_OK);

		if (SUCCEEDED(hr))
		{
			//COLE2T pStr(bstrVersionInfo);
			//MessageBox(NULL, (LPCSTR)pStr, _T("Windows Media Player"), MB_OK);
			MessageBox(NULL, _T("test"), _T("Windows Media Player"), MB_OK);
		}
		else
		{
			MessageBox(NULL, _T("Error"), _T("Windows Media Player"), MB_OK);
		}

	}

	
	MessageBox(NULL, _T("3"), _T("Windows Media Player"), MB_OK);

	// Clean up.
	current_song->Release();
	spPlayer.Release();
	CoUninitialize();
}