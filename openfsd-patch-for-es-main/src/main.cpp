/*
 Copyright (c) 2025 Misaka Nnnnq. All rights reserved.
 */

#include <Windows.h>
#include "EuroScopePlugIn.h"
#include "VATSIMAuthPatch.h"

using namespace EuroScopePlugIn;
using namespace std;

VATSIMAuthPatch* pPlugin;

void __declspec (dllexport) EuroScopePlugInInit(CPlugIn **ppPlugInInstance) {
    *ppPlugInInstance = pPlugin = new VATSIMAuthPatch();
}

void __declspec (dllexport) EuroScopePlugInExit() {
    delete pPlugin;
}
