-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

--create a database in supabase according to this schema

CREATE TABLE public.announcements (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  title text NOT NULL,
  content text,
  created_by uuid,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT announcements_pkey PRIMARY KEY (id),
  CONSTRAINT announcements_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id)
);
CREATE TABLE public.club_members (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  club_id uuid NOT NULL,
  student_id uuid NOT NULL,
  joined_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT club_members_pkey PRIMARY KEY (id),
  CONSTRAINT club_members_club_id_fkey FOREIGN KEY (club_id) REFERENCES public.clubs(id),
  CONSTRAINT club_members_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(user_id)
);
CREATE TABLE public.club_requests (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  club_id uuid,
  student_id uuid,
  status text DEFAULT 'pending'::text CHECK (status = ANY (ARRAY['pending'::text, 'accepted'::text, 'rejected'::text, 'interview'::text])),
  requested_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  interview_date text,
  interview_time text,
  venue text,
  CONSTRAINT club_requests_pkey PRIMARY KEY (id),
  CONSTRAINT club_requests_club_id_fkey FOREIGN KEY (club_id) REFERENCES public.clubs(id),
  CONSTRAINT club_requests_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(user_id)
);
CREATE TABLE public.clubs (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  name text NOT NULL,
  description text,
  club_head_id uuid,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  banner_url text,
  category text DEFAULT 'Other'::text,
  CONSTRAINT clubs_pkey PRIMARY KEY (id),
  CONSTRAINT clubs_club_head_id_fkey FOREIGN KEY (club_head_id) REFERENCES public.users(id)
);
CREATE TABLE public.event_approvals (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  event_id uuid NOT NULL,
  dean_id uuid NOT NULL,
  status text NOT NULL CHECK (status = ANY (ARRAY['pending'::text, 'approved'::text, 'rejected'::text])),
  remarks text,
  reviewed_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT event_approvals_pkey PRIMARY KEY (id),
  CONSTRAINT event_approvals_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.events(id),
  CONSTRAINT event_approvals_dean_id_fkey FOREIGN KEY (dean_id) REFERENCES public.users(id)
);
CREATE TABLE public.event_registrations (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  event_id uuid,
  student_id uuid,
  registered_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  contact text,
  CONSTRAINT event_registrations_pkey PRIMARY KEY (id),
  CONSTRAINT event_registrations_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.events(id),
  CONSTRAINT event_registrations_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(user_id)
);
CREATE TABLE public.events (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  title text NOT NULL,
  description text,
  club_id uuid,
  created_by uuid,
  status text DEFAULT 'pending'::text CHECK (status = ANY (ARRAY['pending'::text, 'approved'::text, 'rejected'::text])),
  event_date timestamp without time zone,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  banner_url text,
  venue text,
  event_time text,
  dean_id uuid,
  entry_fee double precision DEFAULT 0.0,
  is_paid boolean DEFAULT false,
  CONSTRAINT events_pkey PRIMARY KEY (id),
  CONSTRAINT events_club_id_fkey FOREIGN KEY (club_id) REFERENCES public.clubs(id),
  CONSTRAINT events_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id),
  CONSTRAINT events_dean_id_fkey FOREIGN KEY (dean_id) REFERENCES public.users(id)
);
CREATE TABLE public.faculty (
  user_id uuid NOT NULL,
  name text NOT NULL,
  department text,
  contact text,
  photo_url text,
  CONSTRAINT faculty_pkey PRIMARY KEY (user_id),
  CONSTRAINT faculty_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.notifications (
  id uuid NOT NULL DEFAULT uuid_generate_v4(),
  user_id uuid,
  title text NOT NULL,
  message text NOT NULL,
  is_read boolean DEFAULT false,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT notifications_pkey PRIMARY KEY (id),
  CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.point_transactions (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  points integer NOT NULL,
  action_type text NOT NULL CHECK (action_type = ANY (ARRAY['event_registration'::text, 'club_join'::text, 'event_win'::text, 'bonus'::text])),
  reference_id uuid,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT point_transactions_pkey PRIMARY KEY (id),
  CONSTRAINT point_transactions_user_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.students (
  user_id uuid NOT NULL,
  name text NOT NULL,
  enrollment text NOT NULL UNIQUE,
  department text,
  contact text,
  batch text,
  photo_url text,
  Address text,
  CONSTRAINT students_pkey PRIMARY KEY (user_id),
  CONSTRAINT students_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.study_materials (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  faculty_id uuid,
  title text NOT NULL,
  file_url text NOT NULL,
  subject text,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  batch text,
  department text,
  CONSTRAINT study_materials_pkey PRIMARY KEY (id),
  CONSTRAINT study_materials_faculty_id_fkey FOREIGN KEY (faculty_id) REFERENCES public.faculty(user_id)
);
CREATE TABLE public.user_points (
  user_id uuid NOT NULL,
  total_points integer DEFAULT 0,
  updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT user_points_pkey PRIMARY KEY (user_id),
  CONSTRAINT user_points_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.users (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  email text NOT NULL UNIQUE,
  role text NOT NULL CHECK (role = ANY (ARRAY['student'::text, 'faculty'::text, 'club_head'::text, 'dean'::text, 'super_admin'::text])),
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- Point System Triggers and Functions

-- Helper function to award points
CREATE OR REPLACE FUNCTION public.award_points(u_id uuid, pts integer, a_type text, ref_id uuid)
RETURNS void AS $$
BEGIN
    -- Insert transaction log
    INSERT INTO public.point_transactions (user_id, points, action_type, reference_id)
    VALUES (u_id, pts, a_type, ref_id);

    -- Update or initialize user total points
    INSERT INTO public.user_points (user_id, total_points)
    VALUES (u_id, pts)
    ON CONFLICT (user_id) DO UPDATE
    SET total_points = public.user_points.total_points + EXCLUDED.total_points,
        updated_at = CURRENT_TIMESTAMP;
END;
$$ LANGUAGE plpgsql;

-- Trigger function for Event Registration (100 points)
CREATE OR REPLACE FUNCTION public.on_event_registration()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM public.award_points(NEW.student_id, 100, 'event_registration', NEW.id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_event_registration_points
AFTER INSERT ON public.event_registrations
FOR EACH ROW EXECUTE FUNCTION public.on_event_registration();

-- Trigger function for Club Registration Acceptance (100 points)
CREATE OR REPLACE FUNCTION public.on_club_request_accepted()
RETURNS TRIGGER AS $$
BEGIN
    -- Only award points if status transitions to 'accepted'
    IF (OLD.status IS DISTINCT FROM 'accepted' AND NEW.status = 'accepted') THEN
        PERFORM public.award_points(NEW.student_id, 100, 'club_join', NEW.id);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_club_request_accepted_points
AFTER UPDATE ON public.club_requests
FOR EACH ROW EXECUTE FUNCTION public.on_club_request_accepted();
