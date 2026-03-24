import { useState } from 'react';
import { Upload, FileText, Folder, File, Download, Trash2 } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function UploadMaterialScreen() {
  const [subject, setSubject] = useState('');
  const files = [
    { id: 1, name: 'Data Structures - Lecture 1.pdf', subject: 'Computer Science', date: '2024-03-20', size: '2.4 MB', type: 'PDF' },
    { id: 2, name: 'Algorithms - Chapter 3.pdf', subject: 'Computer Science', date: '2024-03-18', size: '1.8 MB', type: 'PDF' },
    { id: 3, name: 'Database Lab Manual.pdf', subject: 'Computer Science', date: '2024-03-15', size: '3.2 MB', type: 'PDF' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Upload Material" showBack showNotifications />
        <div className="px-4 py-6 space-y-6">
          {/* Upload Form Card */}
          <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
            <div className="mb-4">
              <label className="block text-sm font-semibold text-[#1E293B] mb-2">Select Subject</label>
              <div className="relative">
                <Folder className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                <select
                  value={subject}
                  onChange={(e) => setSubject(e.target.value)}
                  className="w-full pl-11 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#F59E0B] bg-white"
                >
                  <option value="">Choose a subject</option>
                  <option value="cs">Computer Science</option>
                  <option value="math">Mathematics</option>
                  <option value="physics">Physics</option>
                </select>
              </div>
            </div>
            <button className="w-full bg-gradient-to-r from-[#F59E0B] to-[#D97706] text-white py-3.5 rounded-xl font-semibold hover:from-[#D97706] hover:to-[#B45309] transition-all flex items-center justify-center gap-2 shadow-sm">
              <Upload className="w-5 h-5" />
              Choose File to Upload
            </button>
          </div>

          {/* Statistics */}
          <div className="grid grid-cols-2 gap-3">
            <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 text-center">
              <FileText className="w-6 h-6 text-[#F59E0B] mx-auto mb-2" />
              <p className="text-2xl font-bold text-[#1E293B] mb-1">{files.length}</p>
              <p className="text-xs text-gray-600">Total Files</p>
            </div>
            <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 text-center">
              <Folder className="w-6 h-6 text-[#F59E0B] mx-auto mb-2" />
              <p className="text-2xl font-bold text-[#1E293B] mb-1">3</p>
              <p className="text-xs text-gray-600">Subjects</p>
            </div>
          </div>

          {/* Uploaded Files */}
          <div>
            <h3 className="text-lg font-semibold text-[#1E293B] mb-3">Uploaded Materials</h3>
            <div className="space-y-3">
              {files.map((file) => (
                <div key={file.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all">
                  <div className="flex items-start gap-3 mb-3">
                    <div className="w-12 h-12 bg-gradient-to-br from-red-100 to-orange-100 rounded-xl flex items-center justify-center flex-shrink-0">
                      <FileText className="w-6 h-6 text-red-600" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <h4 className="font-semibold text-[#1E293B] mb-1 text-sm">{file.name}</h4>
                      <p className="text-xs text-gray-600 mb-1">{file.subject}</p>
                      <div className="flex items-center gap-2">
                        <span className="bg-blue-100 text-blue-700 px-2 py-0.5 rounded text-xs font-medium">
                          {file.type}
                        </span>
                        <span className="text-xs text-gray-500">{file.size}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center justify-between pt-3 border-t border-gray-100">
                    <span className="text-xs text-gray-500">
                      {new Date(file.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                    </span>
                    <div className="flex gap-2">
                      <button className="bg-[#F59E0B]/10 text-[#F59E0B] p-2 rounded-lg hover:bg-[#F59E0B]/20 transition-colors">
                        <Download className="w-4 h-4" />
                      </button>
                      <button className="bg-red-50 text-red-600 p-2 rounded-lg hover:bg-red-100 transition-colors">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
        <BottomNav role="faculty" />
      </div>
    </MobileContainer>
  );
}