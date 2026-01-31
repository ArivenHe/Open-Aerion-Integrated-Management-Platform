cmake_minimum_required(VERSION 3.30)
project(EuroscopePatch)

# --- 核心修复：强制使用静态运行时 /MT ---
# 这会让你的代码与 vcpkg 的 fmt (x86-windows-static) 血液匹配
set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
# ---------------------------------------

set(CMAKE_CXX_STANDARD 20)

# 添加全局定义
add_definitions(-DWIN32_LEAN_AND_MEAN)
add_definitions(-D_CRT_SECURE_NO_WARNINGS) # 建议加上，减少 MSVC 对旧函数的警告
add_definitions(-DWINVER=0x0601)
add_definitions(-D_WIN32_WINNT=0x0601)
add_definitions(-D_AFXDLL)

# 解决 MSVC 编码报错
if(MSVC)
    add_compile_options(/utf-8)
endif()

find_package(fmt CONFIG REQUIRED)

add_library(EuroscopePatch SHARED
        src/EuroScopePlugIn.h
        src/main.cpp
        src/VATSIMAuthPatch.cpp
        src/VATSIMAuthPatch.h
)

target_link_libraries(EuroscopePatch PRIVATE
        fmt::fmt
        "${CMAKE_CURRENT_SOURCE_DIR}/EuroScopePlugInDll.lib"
)