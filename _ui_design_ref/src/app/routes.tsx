import { createBrowserRouter, Navigate } from 'react-router';
import { ErrorBoundary } from './components/ErrorBoundary';

// Starting Screens
import { SplashScreen } from './screens/SplashScreen';
import { LoginScreen } from './screens/LoginScreen';
import { RoleSelectionScreen } from './screens/RoleSelectionScreen';

// Admin Screens
import { AdminDashboard } from './screens/admin/AdminDashboard';
import { ManageStudentsScreen } from './screens/admin/ManageStudentsScreen';
import { ManageFacultyScreen } from './screens/admin/ManageFacultyScreen';
import { AdminAnnouncementsScreen } from './screens/admin/AdminAnnouncementsScreen';
import { EventApprovalScreen } from './screens/admin/EventApprovalScreen';
import { ReportsScreen } from './screens/admin/ReportsScreen';
import { AdminProfileScreen } from './screens/admin/AdminProfileScreen';
import { AdminSettingsScreen } from './screens/admin/AdminSettingsScreen';

// Club Lead Screens
import { ClubLeadDashboard } from './screens/club-lead/ClubLeadDashboard';
import { ClubProfileScreen } from './screens/club-lead/ClubProfileScreen';
import { MemberRequestsScreen } from './screens/club-lead/MemberRequestsScreen';
import { CreateEventScreen } from './screens/club-lead/CreateEventScreen';
import { ClubEventsScreen } from './screens/club-lead/ClubEventsScreen';
import { InterviewSchedulingScreen } from './screens/club-lead/InterviewSchedulingScreen';
import { ClubNotificationsScreen } from './screens/club-lead/ClubNotificationsScreen';
import { ClubSettingsScreen } from './screens/club-lead/ClubSettingsScreen';
import { ClubLeadCalendarScreen } from './screens/club-lead/ClubLeadCalendarScreen';

// Faculty Screens
import { FacultyDashboard } from './screens/faculty/FacultyDashboard';
import { UploadMaterialScreen } from './screens/faculty/UploadMaterialScreen';
import { StudentManagementScreen } from './screens/faculty/StudentManagementScreen';
import { FacultyAnnouncementsScreen } from './screens/faculty/FacultyAnnouncementsScreen';
import { EventsClubsOverviewScreen } from './screens/faculty/EventsClubsOverviewScreen';
import { FacultyProfileScreen } from './screens/faculty/FacultyProfileScreen';
import { FacultySettingsScreen } from './screens/faculty/FacultySettingsScreen';

// Student Screens
import { StudentDashboard } from './screens/student/StudentDashboard';
import { EventsListScreen } from './screens/student/EventsListScreen';
import { EventDetailsScreen } from './screens/student/EventDetailsScreen';
import { ClubsListScreen } from './screens/student/ClubsListScreen';
import { ClubDetailsScreen } from './screens/student/ClubDetailsScreen';
import { JoinClubRequestScreen } from './screens/student/JoinClubRequestScreen';
import { AchievementsScreen } from './screens/student/AchievementsScreen';
import { MyRequestsScreen } from './screens/student/MyRequestsScreen';
import { StudentCalendarScreen } from './screens/student/StudentCalendarScreen';
import { StudentNotificationsScreen } from './screens/student/StudentNotificationsScreen';
import { StudentProfileScreen } from './screens/student/StudentProfileScreen';
import { StudentSettingsScreen } from './screens/student/StudentSettingsScreen';
import { TransportScreen } from './screens/student/TransportScreen';

export const router = createBrowserRouter([
  {
    path: '/',
    Component: SplashScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/login',
    Component: LoginScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/role-selection',
    Component: RoleSelectionScreen,
    errorElement: <ErrorBoundary />,
  },
  // Admin Routes
  {
    path: '/admin/dashboard',
    Component: AdminDashboard,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/admin/students',
    Component: ManageStudentsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/admin/faculty',
    Component: ManageFacultyScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/admin/announcements',
    Component: AdminAnnouncementsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/admin/event-approvals',
    Component: EventApprovalScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/admin/reports',
    Component: ReportsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/admin/profile',
    Component: AdminProfileScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/admin/settings',
    Component: AdminSettingsScreen,
    errorElement: <ErrorBoundary />,
  },
  // Club Lead Routes
  {
    path: '/club-lead/dashboard',
    Component: ClubLeadDashboard,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/club-lead/profile',
    Component: ClubProfileScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/club-lead/member-requests',
    Component: MemberRequestsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/club-lead/create-event',
    Component: CreateEventScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/club-lead/events',
    Component: ClubEventsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/club-lead/interviews',
    Component: InterviewSchedulingScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/club-lead/notifications',
    Component: ClubNotificationsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/club-lead/settings',
    Component: ClubSettingsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/club-lead/calendar',
    Component: ClubLeadCalendarScreen,
    errorElement: <ErrorBoundary />,
  },
  // Faculty Routes
  {
    path: '/faculty/dashboard',
    Component: FacultyDashboard,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/faculty/upload-material',
    Component: UploadMaterialScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/faculty/student-management',
    Component: StudentManagementScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/faculty/announcements',
    Component: FacultyAnnouncementsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/faculty/events-clubs',
    Component: EventsClubsOverviewScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/faculty/profile',
    Component: FacultyProfileScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/faculty/settings',
    Component: FacultySettingsScreen,
    errorElement: <ErrorBoundary />,
  },
  // Student Routes
  {
    path: '/student/dashboard',
    Component: StudentDashboard,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/events',
    Component: EventsListScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/events/:id',
    Component: EventDetailsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/clubs',
    Component: ClubsListScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/clubs/:id',
    Component: ClubDetailsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/join-club',
    Component: JoinClubRequestScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/achievements',
    Component: AchievementsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/requests',
    Component: MyRequestsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/calendar',
    Component: StudentCalendarScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/notifications',
    Component: StudentNotificationsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/profile',
    Component: StudentProfileScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/settings',
    Component: StudentSettingsScreen,
    errorElement: <ErrorBoundary />,
  },
  {
    path: '/student/transport',
    Component: TransportScreen,
    errorElement: <ErrorBoundary />,
  },
]);