import "./BargraphStyle.css"
import { BarChart, Bar, Tooltip, XAxis, YAxis, ResponsiveContainer } from "recharts";

export default function Bargraph({ data }) {
    const fullDayData = Array.from({ length: 24 }, (_, i) => {
        const existing = data?.find(d => d.hour === i);
        return { hour: i, count: existing ? existing.count : 0 };
    });

    return (
        <ResponsiveContainer width="100%" height={630}>
            <BarChart data={fullDayData} margin={{ top: 20, bottom: 30 }}>
                <XAxis
                    dataKey="hour"
                    stroke="#fff"
                    label={{ value: 'Hour', position: 'bottom', fill: '#FFFFFF', dx: -30 }}
                />
                <YAxis
                    stroke="#fff"
                    allowDecimals={false}
                    label={{ value: 'Motion Count', angle: -90, position: 'insideLeft', fill: '#FFFFFF' }}
                />
                <Tooltip />
                <Bar dataKey="count" fill="#FFFFFF" />
            </BarChart>
        </ResponsiveContainer>
    )
}