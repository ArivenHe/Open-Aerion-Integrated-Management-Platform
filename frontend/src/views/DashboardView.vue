<script setup>
import { ref } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  GraphicComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import * as echarts from 'echarts/core'

use([
  CanvasRenderer,
  BarChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  GraphicComponent
])

const userStats = ref({
  avatar: 'https://avatars.githubusercontent.com/u/12345678?v=4', // Placeholder
  name: '飞行员姓名',
  flightHours: '1250h 30m',
  controlHours: '450h 15m',
  connections: 328
})

const platformStats = ref({
  registeredUsers: 12345,
  certifiedControllers: 890,
  activities: 156
})

const createChartOption = (title, data, colorStart, colorEnd) => ({
  title: {
    text: title,
    left: 'left',
    textStyle: {
      color: '#6b7280', // gray-500
      fontSize: 14,
      fontWeight: '600'
    }
  },
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    borderColor: '#e5e7eb',
    textStyle: { color: '#1f2937' },
    extraCssText: 'box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06); border-radius: 8px;'
  },
  grid: {
    left: '0%',
    right: '0%',
    bottom: '0%',
    top: '40px',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: data.map(item => item.name),
    axisTick: { show: false },
    axisLine: { show: false },
    axisLabel: {
      color: '#9ca3af',
      interval: 0,
      margin: 12
    }
  },
  yAxis: {
    type: 'value',
    splitLine: {
      lineStyle: {
        color: '#f3f4f6',
        type: 'dashed'
      }
    },
    axisLabel: { show: false }
  },
  series: [{
    data: data.map(item => item.value),
    type: 'bar',
    barWidth: '40%',
    itemStyle: {
      borderRadius: [4, 4, 0, 0],
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: colorStart },
        { offset: 1, color: colorEnd }
      ])
    },
    showBackground: true,
    backgroundStyle: {
      color: 'rgba(180, 180, 180, 0.1)',
      borderRadius: [4, 4, 0, 0]
    }
  }]
})

const flightHoursOption = ref(createChartOption(
  '飞行时长排行',
  [
    { name: '飞行员 A', value: 1500 },
    { name: '飞行员 B', value: 1450 },
    { name: '飞行员 C', value: 1400 },
    { name: '飞行员 D', value: 1350 },
    { name: '飞行员 E', value: 1300 }
  ],
  '#60a5fa', // blue-400
  '#2563eb'  // blue-600
))

const controlHoursOption = ref(createChartOption(
  '管制时长排行',
  [
    { name: '管制员 A', value: 800 },
    { name: '管制员 B', value: 750 },
    { name: '管制员 C', value: 700 },
    { name: '管制员 D', value: 650 },
    { name: '管制员 E', value: 600 }
  ],
  '#a78bfa', // violet-400
  '#7c3aed'  // violet-600
))

const recentActivity = ref({
  title: '20251227 日常连飞活动',
  route: 'ZSWX 苏南/硕放 ✈ ZYTX 沈阳/桃仙',
  date: '2025-12-27',
  status: '报名中',
  details: {
    routePath: 'JTN W116 PK G455 HSH V2 ODULO B221 XDX W174 FD W172 TEKAM V68 DOBGA W202 UDETI A588 TOSID',
    direction: '向东飞行',
    distance: '770nm',
    navData: '2511',
    rules: 'IFR/VFR',
    airportLevel: '4E',
    aircraft: '波音747、A340、波音787、波音777、空客A330及以下',
    notams: '无'
  }
})
</script>

<template>
  <DashboardLayout>
    <div class="space-y-8">
      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">仪表盘</h1>
          <p class="text-gray-500 dark:text-gray-400 mt-1">平台数据统计与活动概览。</p>
        </div>
        <div class="text-sm text-gray-400 bg-gray-50 dark:bg-gray-800 px-3 py-1 rounded-full border border-gray-200 dark:border-gray-700">
           {{ new Date().toLocaleDateString() }}
        </div>
      </div>

      <!-- User & Platform Stats Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        
        <!-- User Card (Spans 1 col) -->
        <div class="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-100 dark:border-gray-800 shadow-sm flex flex-col items-center justify-center text-center space-y-4">
          <div class="relative">
            <img :src="userStats.avatar" alt="User Avatar" class="w-24 h-24 rounded-full border-4 border-blue-50 dark:border-blue-900/20" />
            <div class="absolute bottom-0 right-0 w-6 h-6 bg-green-500 border-4 border-white dark:border-gray-900 rounded-full"></div>
          </div>
          <div>
            <h2 class="text-lg font-bold text-gray-900 dark:text-white">{{ userStats.name }}</h2>
            <p class="text-sm text-gray-500 dark:text-gray-400">资深飞行员</p>
          </div>
          <div class="w-full grid grid-cols-3 gap-2 pt-4 border-t border-gray-100 dark:border-gray-800">
            <div>
              <div class="text-xs text-gray-400 uppercase tracking-wider mb-1">飞行时长</div>
              <div class="font-bold text-gray-900 dark:text-white whitespace-nowrap">{{ userStats.flightHours }}</div>
            </div>
            <div>
              <div class="text-xs text-gray-400 uppercase tracking-wider mb-1">管制时长</div>
              <div class="font-bold text-gray-900 dark:text-white whitespace-nowrap">{{ userStats.controlHours }}</div>
            </div>
             <div>
              <div class="text-xs text-gray-400 uppercase tracking-wider mb-1">连线次数</div>
              <div class="font-bold text-gray-900 dark:text-white whitespace-nowrap">{{ userStats.connections }}</div>
            </div>
          </div>
        </div>

        <!-- Platform Stats (Spans 3 cols) -->
        <div class="lg:col-span-3 grid grid-cols-1 md:grid-cols-3 gap-6">
           <div class="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-100 dark:border-gray-800 shadow-sm flex flex-col justify-between group hover:border-blue-200 dark:hover:border-blue-800 transition-colors">
              <div class="flex justify-between items-start">
                 <div>
                    <p class="text-sm font-medium text-gray-500 dark:text-gray-400">总用户数</p>
                    <h3 class="text-3xl font-bold text-gray-900 dark:text-white mt-2">{{ platformStats.registeredUsers.toLocaleString() }}</h3>
                 </div>
                 <div class="p-3 bg-blue-50 dark:bg-blue-900/20 rounded-xl text-blue-500 group-hover:bg-blue-100 dark:group-hover:bg-blue-900/40 transition-colors">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
                 </div>
              </div>
              <div class="mt-4 flex items-center text-sm text-green-500">
                 <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path></svg>
                 <span>较上月增长 12%</span>
              </div>
           </div>

           <div class="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-100 dark:border-gray-800 shadow-sm flex flex-col justify-between group hover:border-violet-200 dark:hover:border-violet-800 transition-colors">
              <div class="flex justify-between items-start">
                 <div>
                    <p class="text-sm font-medium text-gray-500 dark:text-gray-400">认证管制员</p>
                    <h3 class="text-3xl font-bold text-gray-900 dark:text-white mt-2">{{ platformStats.certifiedControllers }}</h3>
                 </div>
                 <div class="p-3 bg-violet-50 dark:bg-violet-900/20 rounded-xl text-violet-500 group-hover:bg-violet-100 dark:group-hover:bg-violet-900/40 transition-colors">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                 </div>
              </div>
              <div class="mt-4 flex items-center text-sm text-gray-400">
                 <span>覆盖 12 个管制扇区</span>
              </div>
           </div>

           <div class="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-100 dark:border-gray-800 shadow-sm flex flex-col justify-between group hover:border-orange-200 dark:hover:border-orange-800 transition-colors">
              <div class="flex justify-between items-start">
                 <div>
                    <p class="text-sm font-medium text-gray-500 dark:text-gray-400">活动总数</p>
                    <h3 class="text-3xl font-bold text-gray-900 dark:text-white mt-2">{{ platformStats.activities }}</h3>
                 </div>
                 <div class="p-3 bg-orange-50 dark:bg-orange-900/20 rounded-xl text-orange-500 group-hover:bg-orange-100 dark:group-hover:bg-orange-900/40 transition-colors">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
                 </div>
              </div>
               <div class="mt-4 flex items-center text-sm text-orange-500">
                 <span class="inline-block w-2 h-2 bg-orange-500 rounded-full mr-2 animate-pulse"></span>
                 <span>1 个活动进行中</span>
              </div>
           </div>
        </div>
      </div>

      <!-- Charts Section -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-100 dark:border-gray-800 shadow-sm h-96">
          <v-chart class="w-full h-full" :option="flightHoursOption" autoresize />
        </div>
        <div class="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-100 dark:border-gray-800 shadow-sm h-96">
          <v-chart class="w-full h-full" :option="controlHoursOption" autoresize />
        </div>
      </div>

      <!-- Recent Activity Card (Styled) -->
      <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-100 dark:border-gray-800 shadow-sm overflow-hidden">
         <!-- Card Header -->
         <div class="p-6 border-b border-gray-100 dark:border-gray-800 flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-gray-50/50 dark:bg-gray-800/20">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 bg-red-500/10 text-red-500 rounded-xl flex items-center justify-center border border-red-500/20">
                 <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path></svg>
              </div>
              <div>
                 <div class="flex items-center gap-2">
                    <h3 class="text-lg font-bold text-gray-900 dark:text-white">{{ recentActivity.title }}</h3>
                    <span class="px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border border-green-200 dark:border-green-800">
                      {{ recentActivity.status }}
                    </span>
                 </div>
                 <div class="text-sm text-gray-500 dark:text-gray-400 mt-1 font-mono">
                    {{ recentActivity.date }} <span class="mx-2">•</span> {{ recentActivity.route }}
                 </div>
              </div>
            </div>
            <button class="px-4 py-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
               查看详情
            </button>
         </div>

         <!-- Card Content (Table-like grid) -->
         <div class="divide-y divide-gray-100 dark:divide-gray-800 text-sm">
            <div class="grid grid-cols-1 md:grid-cols-12">
               <div class="md:col-span-3 lg:col-span-2 p-4 bg-gray-50/50 dark:bg-gray-800/30 text-gray-500 dark:text-gray-400 font-medium flex items-center">航路</div>
               <div class="md:col-span-9 lg:col-span-10 p-4 text-gray-900 dark:text-gray-200 font-mono break-all leading-relaxed">
                  {{ recentActivity.details.routePath }}
               </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-12">
               <div class="md:col-span-3 lg:col-span-2 p-4 bg-gray-50/50 dark:bg-gray-800/30 text-gray-500 dark:text-gray-400 font-medium flex items-center">飞行信息</div>
               <div class="md:col-span-9 lg:col-span-10 p-0 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 divide-y sm:divide-y-0 sm:divide-x divide-gray-100 dark:divide-gray-800">
                  <div class="p-4 flex flex-col">
                     <span class="text-xs text-gray-400 mb-1">飞行方向</span>
                     <span class="font-medium text-gray-900 dark:text-white">{{ recentActivity.details.direction }}</span>
                  </div>
                  <div class="p-4 flex flex-col">
                     <span class="text-xs text-gray-400 mb-1">飞行距离</span>
                     <span class="font-medium text-gray-900 dark:text-white">{{ recentActivity.details.distance }}</span>
                  </div>
                  <div class="p-4 flex flex-col">
                     <span class="text-xs text-gray-400 mb-1">导航数据</span>
                     <span class="font-medium text-gray-900 dark:text-white">{{ recentActivity.details.navData }}</span>
                  </div>
                  <div class="p-4 flex flex-col">
                     <span class="text-xs text-gray-400 mb-1">飞行规则</span>
                     <span class="font-medium text-gray-900 dark:text-white">{{ recentActivity.details.rules }}</span>
                  </div>
               </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-12">
               <div class="md:col-span-3 lg:col-span-2 p-4 bg-gray-50/50 dark:bg-gray-800/30 text-gray-500 dark:text-gray-400 font-medium flex items-center">活动要求</div>
               <div class="md:col-span-9 lg:col-span-10 p-4 text-gray-900 dark:text-gray-200">
                  <div class="flex flex-col gap-2">
                     <div>
                        <span class="text-xs text-gray-400 uppercase tracking-wide">机场等级：</span>
                        <span class="ml-2 font-medium">{{ recentActivity.details.airportLevel }}</span>
                     </div>
                     <div>
                        <span class="text-xs text-gray-400 uppercase tracking-wide">机型限制：</span>
                        <span class="ml-2">{{ recentActivity.details.aircraft }}</span>
                     </div>
                  </div>
               </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-12">
               <div class="md:col-span-3 lg:col-span-2 p-4 bg-gray-50/50 dark:bg-gray-800/30 text-gray-500 dark:text-gray-400 font-medium flex items-center">航行通告</div>
               <div class="md:col-span-9 lg:col-span-10 p-4 text-gray-900 dark:text-gray-200">
                  {{ recentActivity.details.notams }}
               </div>
            </div>
         </div>
      </div>

    </div>
  </DashboardLayout>
</template>
