/*
 Copyright (c) 2025 Misaka Nnnnq. All rights reserved.
 */

#include "VATSIMAuthPatch.h"

#include <vector>
#include <fmt/format.h>
#include <Psapi.h>
#include "config.h"

std::vector<int> buildKMPTable(const std::string& pattern) {
    int m = pattern.length();
    std::vector<int> kmpTable(m, 0);
    int j = 0;

    for (int i = 1; i < m; ++i) {
        while (j > 0 && pattern[i] != pattern[j]) {
            j = kmpTable[j - 1];
        }
        if (pattern[i] == pattern[j]) {
            ++j;
        }
        kmpTable[i] = j;
    }

    return kmpTable;
}

int KMPSearch(const std::string& text, const std::string& pattern) {
    std::vector<int> kmpTable = buildKMPTable(pattern);
    int n = text.length();
    int m = pattern.length();
    int j = 0;

    for (int i = 0; i < n; ++i) {
        while (j > 0 && text[i] != pattern[j]) {
            j = kmpTable[j - 1];
        }
        if (text[i] == pattern[j]) {
            ++j;
        }
        if (j == m) {
            return i - m + 1;
        }
    }

    return -1;
}

const string MY_PLUGIN_NAME = "Openfsd Patch Plugin";
const string MY_PLUGIN_VERSION = "1.0.0";
const string MY_PLUGIN_DEVELOPER = "William He(Misaka)";
const string MY_PLUGIN_COPYRIGHT = "Copyright (C) 2023-2025";


VATSIMAuthPatch::VATSIMAuthPatch() :
    CPlugIn(COMPATIBILITY_CODE,
        MY_PLUGIN_NAME.c_str(),
        MY_PLUGIN_VERSION.c_str(),
        MY_PLUGIN_DEVELOPER.c_str(),
        MY_PLUGIN_COPYRIGHT.c_str())
{
    HMODULE hModule = GetModuleHandle("EuroScope.exe");
    if (hModule == nullptr) {
        DisplayUserMessage("Openfsd-patch", "Plugin", "Failed to GetModuleHandle, please contact us for help.", 1, 1, 1, 1, 1);
        return ;
    }


    auto dosHeader  = reinterpret_cast<PIMAGE_DOS_HEADER>(hModule);
    if (dosHeader->e_magic != IMAGE_DOS_SIGNATURE) {
        DisplayUserMessage("Openfsd-patch", "Plugin", "Invalid DOS header, please contact us for help.", 1, 1, 1, 1, 1);
        return;
    }

    auto ntHeaders = reinterpret_cast<PIMAGE_NT_HEADERS>(reinterpret_cast<BYTE *>(hModule) + dosHeader->e_lfanew);
    if (ntHeaders->Signature != IMAGE_NT_SIGNATURE) {
        DisplayUserMessage("Openfsd-patch", "Plugin", "Invalid PE header, please contact us for help.", 1, 1, 1, 1, 1);
        return;
    }

    PIMAGE_SECTION_HEADER sectionHeader = IMAGE_FIRST_SECTION(ntHeaders);

    for (int i = 0; i < ntHeaders->FileHeader.NumberOfSections; i++) {
        if (strncmp((const char*)sectionHeader[i].Name, ".rdata", 6) == 0) {
            DWORD rdataRVA = sectionHeader[i].VirtualAddress;
            DWORD rdataSize = sectionHeader[i].SizeOfRawData;
            DWORD baseAddress = (DWORD)hModule;

            void* rdataAddress = (void*)(baseAddress + rdataRVA);

            DWORD oldProtect;
            VirtualProtect(rdataAddress, rdataSize, PAGE_EXECUTE_READWRITE, &oldProtect);

            string rdataContent((char*)rdataAddress, rdataSize);
            string pattern = "https://auth.vatsim.net/api/fsd-jwt";

            auto rs = KMPSearch(rdataContent, pattern);
            if (rs != -1) {
                strcpy(reinterpret_cast<char *>(reinterpret_cast<uintptr_t>(rdataAddress) + rs), TARGET_JWT_URL);
            }

            DisplayUserMessage("Openfsd-patch", "Plugin", fmt::format("Found offset at {}", rs).c_str(), 1, 1, 0, 0, 0);

            VirtualProtect(rdataAddress, rdataSize, oldProtect, &oldProtect);
            return;
        }
    }
}
