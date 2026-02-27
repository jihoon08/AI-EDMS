'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { aiApi, type QaResponse } from '@/lib/api/ai';
import { Send, ThumbsUp, ThumbsDown, Bot, User } from 'lucide-react';
import { useState, useRef, useEffect } from 'react';

export default function AiPage() {
  const queryClient = useQueryClient();
  const [question, setQuestion] = useState('');
  const chatEndRef = useRef<HTMLDivElement>(null);

  const { data: history } = useQuery({
    queryKey: ['qa-history'],
    queryFn: () => aiApi.getQaHistory({ size: 50 }),
    select: (res) => res.data.data?.content ?? [],
  });

  const askMutation = useMutation({
    mutationFn: (q: string) => aiApi.askQuestion(q),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['qa-history'] });
      setQuestion('');
    },
  });

  const feedbackMutation = useMutation({
    mutationFn: ({ qaUuid, feedback }: { qaUuid: string; feedback: string }) =>
      aiApi.setFeedback(qaUuid, feedback),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['qa-history'] });
    },
  });

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [history]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!question.trim() || askMutation.isPending) return;
    askMutation.mutate(question.trim());
  };

  const reversedHistory = [...(history ?? [])].reverse();

  return (
    <div className="flex h-[calc(100vh-8rem)] flex-col">
      <h1 className="mb-4 text-2xl font-bold text-gray-900">AI 어시스턴트</h1>

      {/* 채팅 영역 */}
      <div className="flex-1 overflow-y-auto rounded-lg border bg-white p-4">
        {reversedHistory.length === 0 ? (
          <div className="flex h-full flex-col items-center justify-center text-gray-400">
            <Bot size={48} className="mb-4" />
            <p className="text-lg font-medium">AI 어시스턴트에게 질문하세요</p>
            <p className="mt-1 text-sm">문서 내용, 업무 관련 질문 등을 자연어로 물어볼 수 있습니다</p>
          </div>
        ) : (
          <div className="space-y-4">
            {reversedHistory.map((qa: QaResponse) => (
              <div key={qa.qaUuid} className="space-y-3">
                {/* 질문 */}
                <div className="flex justify-end">
                  <div className="flex items-start gap-2">
                    <div className="max-w-lg rounded-lg bg-blue-600 px-4 py-2 text-sm text-white">
                      {qa.question}
                    </div>
                    <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-blue-100">
                      <User size={16} className="text-blue-700" />
                    </div>
                  </div>
                </div>

                {/* 답변 */}
                <div className="flex justify-start">
                  <div className="flex items-start gap-2">
                    <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-purple-100">
                      <Bot size={16} className="text-purple-700" />
                    </div>
                    <div className="max-w-lg">
                      <div className="rounded-lg bg-gray-100 px-4 py-2 text-sm text-gray-800">
                        {qa.answer}
                      </div>

                      {/* 피드백 */}
                      <div className="mt-1 flex items-center gap-2">
                        <button
                          onClick={() => feedbackMutation.mutate({ qaUuid: qa.qaUuid, feedback: 'HELPFUL' })}
                          className={`rounded p-1 ${qa.feedback === 'HELPFUL' ? 'bg-green-100 text-green-600' : 'text-gray-400 hover:text-green-600'}`}
                        >
                          <ThumbsUp size={14} />
                        </button>
                        <button
                          onClick={() => feedbackMutation.mutate({ qaUuid: qa.qaUuid, feedback: 'NOT_HELPFUL' })}
                          className={`rounded p-1 ${qa.feedback === 'NOT_HELPFUL' ? 'bg-red-100 text-red-600' : 'text-gray-400 hover:text-red-600'}`}
                        >
                          <ThumbsDown size={14} />
                        </button>
                        <span className="text-[10px] text-gray-400">
                          {new Date(qa.createdAt).toLocaleString('ko-KR')}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
            <div ref={chatEndRef} />
          </div>
        )}
      </div>

      {/* 입력 영역 */}
      <form onSubmit={handleSubmit} className="mt-4 flex gap-2">
        <input
          type="text"
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder="문서에 대해 질문하세요... (예: 작년 서버 계약 비용은?)"
          className="flex-1 rounded-lg border px-4 py-3 text-sm focus:border-blue-500 focus:outline-none"
          disabled={askMutation.isPending}
        />
        <button
          type="submit"
          disabled={!question.trim() || askMutation.isPending}
          className="rounded-lg bg-blue-600 px-4 py-3 text-white hover:bg-blue-700 disabled:opacity-50"
        >
          <Send size={20} />
        </button>
      </form>
    </div>
  );
}
