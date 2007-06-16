#include "TransparencyManager.h"
#include <jawt_md.h>

#define LWA_ALPHA 0x00000002

#ifdef WIN32

#ifndef WS_EX_LAYERED

#define WS_EX_LAYERED 0x00080000
#define LWA_COLORKEY 0x00000001

#endif // ndef WS_EX_LAYERED


// Preparation for the function we want to import from USER32.DLL
typedef BOOL (WINAPI *lpfnSetLayeredWindowAttributes)(HWND hWnd, COLORREF crKey, BYTE bAlpha, DWORD dwFlags);
lpfnSetLayeredWindowAttributes SetLayeredWindowAttributes;
typedef BOOL (WINAPI *lpfnGetLayeredWindowAttributes)(HWND hWnd, COLORREF* crKey, BYTE* bAlpha, DWORD* dwFlags);
lpfnGetLayeredWindowAttributes GetLayeredWindowAttributes;

HMODULE hUser32 = NULL;
/* Cache the handle to the library */
HINSTANCE g_hInstance = NULL;

BOOL WINAPI DllMain(HANDLE hInstDLL, DWORD dwReason, LPVOID lpvReserved){
	if (dwReason == DLL_PROCESS_ATTACH) {
		if (g_hInstance == NULL) g_hInstance = hInstDLL;
		// Here we import the function from USER32.DLL
		hUser32 = GetModuleHandle("USER32.DLL");
		if (hUser32 != NULL){
			SetLayeredWindowAttributes = (lpfnSetLayeredWindowAttributes)GetProcAddress(hUser32, "SetLayeredWindowAttributes");
			GetLayeredWindowAttributes = (lpfnGetLayeredWindowAttributes)GetProcAddress(hUser32, "GetLayeredWindowAttributes");
		} else 
			return FALSE;
	}
	return TRUE;
}

/* Natives */
#define OS_NATIVE(method) Java_org_noos_xing_mydoggy_plaf_ui_transparency_WindowTransparencyManager_##method

JNIEXPORT jboolean JNICALL OS_NATIVE(isAlphaModeEnabledNative)
				   (JNIEnv * env, jclass obj, jobject component) {

	/* Declarations */
	HWND hWnd;
	JAWT awt;
	JAWT_DrawingSurface* ds;
	JAWT_DrawingSurfaceInfo* dsi;
	JAWT_Win32DrawingSurfaceInfo* dsi_win;

	/* Get the AWT */
	awt.version = JAWT_VERSION_1_3;
	if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
		//printf("Cannot get awt!!!");
		return FALSE;
	}

	/* Get the drawing surface */
	ds = awt.GetDrawingSurface(env, component);
	if (ds == 0) {
		//printf("Cannot get DrawingSurface!!!");
		return FALSE;
	}

	/* Lock the drawing surface */
	if ((ds->Lock(ds) & JAWT_LOCK_ERROR) != 0) {
		//printf("isAlphaModeEnabledNative : Cannot lock DrawingSurface!!!");
		return FALSE;
	}

	/* Get the drawing surface info */
	dsi = ds->GetDrawingSurfaceInfo(ds);
	if (dsi == 0) {
		//printf("Cannot get DrawingSurfaceInfo!!!");
		return FALSE;
	}
	
	/* Get the platform-specific drawing info */
	dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;
	hWnd = dsi_win->hwnd;
	if (hWnd == NULL) {
		//printf("Cannot get hWnd!!!");
		return FALSE;
	}		

	if (GetLayeredWindowAttributes(hWnd, NULL, NULL, NULL) != 0) {
		return TRUE;
	} else {
		return FALSE;
	}

	/* Free the drawing surface info */
	ds->FreeDrawingSurfaceInfo(dsi);
	/* Unlock the drawing surface */
	ds->Unlock(ds);
	/* Free the drawing surface */
	awt.FreeDrawingSurface(ds);
}

JNIEXPORT void JNICALL OS_NATIVE(setAlphaModeEnabledNative)
					   (JNIEnv *env, jclass object, jobject component, jboolean alphaMode) {
	/* Declarations */
	HWND hWnd;
	JAWT awt;
	JAWT_DrawingSurface* ds;
	JAWT_DrawingSurfaceInfo* dsi;
	JAWT_Win32DrawingSurfaceInfo* dsi_win;

	/* Get the AWT */
	awt.version = JAWT_VERSION_1_3;
	if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
		//printf("Cannot get awt!!!");
		return;
	}

	/* Get the drawing surface */
	ds = awt.GetDrawingSurface(env, component);
	if (ds == 0) {
		//printf("Cannot get DrawingSurface!!!");
		return;
	}

	/* Lock the drawing surface */
	if ((ds->Lock(ds) & JAWT_LOCK_ERROR) != 0) {
		//printf("setAlphaModeEnabledNative : Cannot lock DrawingSurface!!!");
		return;
	}

	/* Get the drawing surface info */
	dsi = ds->GetDrawingSurfaceInfo(ds);
	if (dsi == 0) {
		//printf("Cannot get DrawingSurfaceInfo!!!");
		return;
	}
	
	/* Get the platform-specific drawing info */
	dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;
	hWnd = dsi_win->hwnd;
	if (hWnd == NULL) {
		//printf("Cannot get hWnd!!!");
		return;
	}
	
	if (alphaMode == TRUE) {
		if (GetLayeredWindowAttributes(hWnd, NULL, NULL, NULL) == 0) {
			// Set WS_EX_LAYERED on this window
			SetWindowLong(hWnd, GWL_EXSTYLE, GetWindowLong(hWnd, GWL_EXSTYLE) | WS_EX_LAYERED);

			// Make this window percent alpha
			SetLayeredWindowAttributes(hWnd, 0, (BYTE) 255, LWA_ALPHA);
		}
	} else {
		// Remove WS_EX_LAYERED from this window styles
		SetWindowLong(hWnd, GWL_EXSTYLE, GetWindowLong(hWnd, GWL_EXSTYLE) & ~WS_EX_LAYERED);
		
		// Ask the window and its children to repaint
		RedrawWindow(hWnd, NULL, NULL, RDW_ERASE | RDW_INVALIDATE | RDW_FRAME | RDW_ALLCHILDREN);
	}

	/* Free the drawing surface info */
	ds->FreeDrawingSurfaceInfo(dsi);
	/* Unlock the drawing surface */
	ds->Unlock(ds);
	/* Free the drawing surface */
	awt.FreeDrawingSurface(ds);
}

JNIEXPORT void JNICALL OS_NATIVE(setAlphaModeRatioNative)
					    (JNIEnv* env, jclass obj, jobject component, jfloat alpha) {
	/* Declarations */
	HWND hWnd;
	JAWT awt;
	JAWT_DrawingSurface* ds;
	JAWT_DrawingSurfaceInfo* dsi;
	JAWT_Win32DrawingSurfaceInfo* dsi_win;

	/* Get the AWT */
	awt.version = JAWT_VERSION_1_3;
	if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
		//printf("Cannot get awt!!!");
		return;
	}

	/* Get the drawing surface */
	ds = awt.GetDrawingSurface(env, component);
	if (ds == 0) {
		//printf("Cannot get DrawingSurface!!!");
		return;
	}

	/* Lock the drawing surface */
	if ((ds->Lock(ds) & JAWT_LOCK_ERROR) != 0) {
		//printf("setAlphaModeRatioNative : Cannot lock DrawingSurface!!!");
		return;
	}

	/* Get the drawing surface info */
	dsi = ds->GetDrawingSurfaceInfo(ds);
	if (dsi == 0) {
		//printf("Cannot get DrawingSurfaceInfo!!!");
		return;
	}
	
	/* Get the platform-specific drawing info */
	dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;
	hWnd = dsi_win->hwnd;
	if (hWnd == NULL) {
		//printf("Cannot get hWnd!!!");
		return;
	}
	
	if ((alpha <= 1.0) && (alpha > 0.0) ) {
		if (GetLayeredWindowAttributes(hWnd, NULL, NULL, NULL) != 0) {
			// Set WS_EX_LAYERED on this window
			SetWindowLong(hWnd, GWL_EXSTYLE, GetWindowLong(hWnd, GWL_EXSTYLE) | WS_EX_LAYERED);

			// Make this window percent alpha
			SetLayeredWindowAttributes(hWnd, 0, (BYTE) (255 * alpha), LWA_ALPHA);
		} 
	}

	/* Free the drawing surface info */
	ds->FreeDrawingSurfaceInfo(dsi);
	/* Unlock the drawing surface */
	ds->Unlock(ds);
	/* Free the drawing surface */
	awt.FreeDrawingSurface(ds);
}

#endif //WIN32

